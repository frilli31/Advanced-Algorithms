import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SerialKMeans {
    static Set<Cluster> run(List<City> cities, int number_of_centers, int interactions) {
        List<Cluster> clusters = cities.stream()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .map(Cluster::new)
                .collect(Collectors.toList());

        final int estimated_size = cities.size() * 2 / number_of_centers;
        Cluster nearestCluster = null;
        int min_distance;
        int distance;

        for (int i = 0; i < interactions; i++) {
            clusters.forEach(cluster -> cluster.cleanCounties(estimated_size));

            for (City city : cities) {
                min_distance = Integer.MAX_VALUE;
                for (Cluster cluster : clusters) {
                    distance = cluster.distance(city);
                    if (distance < min_distance) {
                        nearestCluster = cluster;
                        min_distance = distance;
                    }
                }
                nearestCluster.insert(city);
            }
            clusters.forEach(Cluster::updateCentroid);
        }
        return clusters.stream()
                .filter(cluster -> cluster.cities.size() > 0)
                .collect(Collectors.toSet());
    }
}
