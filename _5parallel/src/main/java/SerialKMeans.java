import java.util.*;
import java.util.stream.Collectors;

public class SerialKMeans {
    static Set<Cluster> run(List<City> cities, int number_of_centers, int interactions) {
        int size = cities.size();
        List<Cluster> initial_clusters = cities.stream()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .map(centroid -> new Cluster(centroid, size * 2 / number_of_centers))
                .collect(Collectors.toList());

        for (int i = 0; i < interactions; i++) {
            if (i != 0)
                for (Cluster cluster : initial_clusters) {
                    cluster.centroid = cluster.getCentroid();
                    cluster.cities = new ArrayList<>(size * 2 / number_of_centers); // enough space to not re-allocate (I hope)
                }

            for (City city : cities) {
                Cluster nearestCluster = null;
                double min_distance = Double.MAX_VALUE;
                for (Cluster cluster : initial_clusters) {
                    double distance = cluster.distance(city);
                    if (distance < min_distance) {
                        nearestCluster = cluster;
                        min_distance = distance;
                    }
                }
                // Cluster nearestCluster = my_clustering.stream()
                //         .min(Comparator.comparingDouble(x -> x.distance(city)))
                //         .get();
                nearestCluster.insert(city);
            }
        }
        for (Cluster cluster : initial_clusters)
            cluster.centroid = cluster.getCentroid();
        return new HashSet<>(initial_clusters);
    }
}
