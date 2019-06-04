import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SerialKMeans {
    static Set<Cluster> run(List<City> cities, int number_of_centers, int iteractions) {
        List<Set<Cluster>> clusterings = new ArrayList<>();

        Set<Cluster> initial_clusters = cities.stream()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .map(Cluster::new)
                .collect(Collectors.toSet());

        clusterings.add(initial_clusters);

        for (int i = 0; i < iteractions; i++) {
            Set<Cluster> my_clustering = clusterings.get(i);

            for (City city : cities) {
                Cluster nearestCluster = null;
                double min_distance = Double.MAX_VALUE;
                for (Cluster cluster : my_clustering) {
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

            clusterings.add(my_clustering.stream().map(Cluster::getCentroid).map(Cluster::new).collect(Collectors.toSet()));
        }
        clusterings.get(iteractions - 1).forEach(cluster -> cluster.centroid = cluster.getCentroid());
        return clusterings.get(iteractions - 1);
    }
}
