import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelKMeans {
    static Set<Cluster> run(List<City> cities, int number_of_centers, int interactions, int cutoff) {
        int size = cities.size();
        List<Centroid> initial_clusters = cities.stream()
                .parallel()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .collect(Collectors.toList());

        ArrayList<Integer> cluster_of_city = (ArrayList<Integer>) IntStream.generate(() -> 0).limit(size).boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < interactions; i++) {

            IntStream.range(0, size)
                    .parallel()
                    .forEach(index -> {
                        int nearestCluster = -1;
                        double min_distance = Double.MAX_VALUE;
                        for (int i_centroid = 0; i_centroid < number_of_centers; i_centroid++) {
                            int distance = initial_clusters.get(i_centroid).distance(cities.get(index));
                            if (distance < min_distance) {
                                nearestCluster = i_centroid;
                                min_distance = distance;
                            }
                        }
                        //int nearestCluster = IntStream.range(0, number_of_centers)
                        //        .boxed()
                        //        .min(Comparator.comparingDouble(x -> initial_clusters.get(x).distance(cities.get(index))))
                        //        .orElseThrow();

                        cluster_of_city.set(index, nearestCluster);
                    });

            IntStream.range(0, number_of_centers)
                    .parallel()
                    .forEach(index_of_center -> {
                        Result r = new ClusterReduce(cluster_of_city, cities, index_of_center, cutoff).invoke();
                        double mid_latitude = r.latitude / r.size;
                        double mid_longitude = r.longitude / r.size;
                        initial_clusters.set(index_of_center, new Centroid(mid_longitude, mid_latitude));
                    });
        }

        Set<Cluster> clustering = new HashSet<>(number_of_centers);

        IntStream.range(0, number_of_centers).parallel()
                .forEach(index_of_cluster -> {
                    List<City> cluster = IntStream.range(0, size)
                            .filter(idx -> cluster_of_city.get(idx) == index_of_cluster)
                            .mapToObj(cities::get)
                            .collect(Collectors.toList());
                    clustering.add(new Cluster(cluster));
                });
        return clustering;
    }

    static Result sequentialReduceCluster(List<Integer> cluster_of_counties, List<City> cities, int h) {
        Result r = new Result();

        for (int i = 0; i < cities.size(); i++) {
            if (cluster_of_counties.get(i) == h) {
                City city = cities.get(i);
                r.size += 1;
                r.longitude += city.getLon();
                r.latitude += city.getLat();
            }
        }
        return r;

        //return IntStream.range(0, cluster_of_counties.size())
        //        .boxed()
        //        .filter(i -> cluster_of_counties.get(i) == h)
        //        .map(idx -> new Result(cities.get(idx)))
        //        .reduce(new Result(), Result::sum);
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


