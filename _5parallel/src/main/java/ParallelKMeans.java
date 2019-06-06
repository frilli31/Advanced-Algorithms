import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelKMeans {
    static Set<Cluster> run(List<City> cities, int number_of_centers, int interactions, int cutoff) {
        final int size = cities.size();
        List<Centroid> initial_clusters = cities.parallelStream()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .collect(Collectors.toList());

        ArrayList<Integer> cluster_of_city = (ArrayList<Integer>) IntStream.generate(() -> 0).limit(size).boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < interactions; i++) {
            BitSet cluster_updated = new BitSet(number_of_centers);

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
                        cluster_updated.set(nearestCluster);
                        cluster_of_city.set(index, nearestCluster);
                    });

            IntStream.range(0, number_of_centers)
                    .parallel()
                    .filter(cluster_updated::get)
                    .forEach(index_of_center -> {
                        Result r = new ClusterReduce(cluster_of_city, cities, index_of_center, cutoff).invoke();
                        double mid_latitude = r.latitude / r.size;
                        double mid_longitude = r.longitude / r.size;
                        initial_clusters.set(index_of_center, new Centroid(mid_latitude, mid_longitude));
                    });
        }

        Set<Cluster> clustering = new HashSet<>(number_of_centers);

        IntStream.range(0, number_of_centers)
                .forEach(index_of_cluster -> {
                    List<City> cluster = IntStream.range(0, size)
                            .filter(idx -> cluster_of_city.get(idx) == index_of_cluster)
                            .mapToObj(cities::get)
                            .collect(Collectors.toList());
                    clustering.add(new Cluster(cluster));
                });
        return clustering;
    }

    static Result sequentialReduceCluster(List<Integer> cluster_of_cities, List<City> cities, int h) {
        Result r = new Result();

        for (int i = 0; i < cluster_of_cities.size(); i++) {
            if (cluster_of_cities.get(i) == h) {
                City city = cities.get(i);
                r.size += 1;
                r.longitude += city.getLon();
                r.latitude += city.getLat();
            }
        }
        return r;
    }

    static class ClusterReduce extends RecursiveTask<Result> {
        final List<Integer> cluster_of_cities;
        final List<City> cities;
        final int h;
        final int cutoff;

        ClusterReduce(List<Integer> cluster_of_cities, List<City> cities, int h, int cutoff) {
            this.cluster_of_cities = cluster_of_cities;
            this.cities = cities;
            this.h = h;
            this.cutoff = cutoff;
        }

        @Override
        public Result compute() {
            final int size = cluster_of_cities.size();

            if (size <= cutoff) {
                return sequentialReduceCluster(cluster_of_cities, cities, h);
            } else {
                int mid = Math.floorDiv(size, 2);
                ClusterReduce r1 = new ClusterReduce(cluster_of_cities.subList(0, mid), cities.subList(0, mid), h, cutoff);
                ClusterReduce r2 = new ClusterReduce(cluster_of_cities.subList(mid, size), cities.subList(mid, size), h, cutoff);
                r1.fork();
                return Result.sum(r2.compute(), r1.join());
            }
        }
    }

    static class Result {
        double latitude;
        double longitude;
        int size;

        static Result sum(Result r1, Result r2) {
            r1.latitude += r2.latitude;
            r1.longitude += r2.longitude;
            r1.size += r2.size;
            return r1;
        }
    }
}