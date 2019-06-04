import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelKMeans {
    static Set<Cluster> run(List<City> counties, int number_of_centers, int iteractions, int cutoff) {
        int size = counties.size();
        List<Centroid> initial_clusters = counties.stream()
                .parallel()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .collect(Collectors.toList());

        ArrayList<Integer> cluster_of_city = (ArrayList<Integer>) IntStream.generate(() -> 0).limit(size).boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < iteractions; i++) {

            IntStream.range(0, size)
                    .parallel()
                    .forEach(index -> {
                        int nearestCluster = IntStream.range(0, number_of_centers)
                                .boxed()
                                .min(Comparator.comparingDouble(x -> initial_clusters.get(x).distance(counties.get(index))))
                                .orElseThrow();
                        cluster_of_city.set(index, nearestCluster);
            });

            IntStream.range(0, number_of_centers)
                    .parallel()
                    .forEach(index_of_center -> {
                        Result r = new ClusterReduce(cluster_of_city, counties, index_of_center, cutoff).invoke();
                        double mid_latitude = r.latitude / r.size;
                        double mid_longitude = r.longitude / r.size;
                        initial_clusters.set(index_of_center, new Centroid(mid_latitude, mid_longitude));
                    });
        }

        Set<Cluster> clusterings = new HashSet<>();

        IntStream.range(0, number_of_centers).forEach(index_of_cluster -> {
            Set<City> cluster = IntStream.range(0, size)
                    .filter(idx -> cluster_of_city.get(idx) == index_of_cluster)
                    .mapToObj(counties::get)
                    .collect(Collectors.toSet());
            clusterings.add(new Cluster(cluster));
        });
        return clusterings;
    }

    static Result sequentialReduceCluster(List<Integer> cluster_of_counties, List<City> cities, int h) {
        if (h == 1)
            return new Result(cities.get(0));

        return IntStream.range(0, cluster_of_counties.size())
                .boxed()
                .filter(i -> cluster_of_counties.get(i) == h)
                .map(idx -> new Result(cities.get(idx)))
                .reduce(new Result(), Result::sum);
    }

    static class ClusterReduce extends RecursiveTask<Result> {
        List<Integer> cluster_of_counties;
        List<City> cities;
        int h;
        int cutoff;

        ClusterReduce(List<Integer> cluster_of_counties, List<City> cities, int h, int cutoff) {
            this.cluster_of_counties = cluster_of_counties;
            this.cities = cities;
            this.h = h;
            this.cutoff = cutoff;
        }

        @Override
        public Result compute() {
            int size = cluster_of_counties.size();

            if (size <= cutoff) {
                return sequentialReduceCluster(cluster_of_counties, cities, h);
            } else {
                int mid = Math.floorDiv(size, 2);
                ClusterReduce r1 = new ClusterReduce(cluster_of_counties.subList(0, mid), cities.subList(0, mid), h, cutoff);
                ClusterReduce r2 = new ClusterReduce(cluster_of_counties.subList(mid, size), cities.subList(mid, size), h, cutoff);
                r1.fork();
                return Result.sum(r2.compute(), r1.join());
            }
        }
    }

    static class Result {
        double latitude;
        double longitude;
        int size;

        Result() {
        }

        Result(City city) {
            this.latitude = city.getLat();
            this.longitude = city.getLon();
            this.size = 1;
        }

        static Result sum(Result r1, Result r2) {
            r1.latitude += r2.latitude;
            r1.longitude += r2.longitude;
            r1.size += r2.size;
            return r1;
        }
    }
}


