import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class ClusteringKMeans {
    static Set<Cluster> run(Set<County> counties, int number_of_centers, int iteractions) {
        Set<Cluster> clusters = counties.stream()
                .sorted(Comparator.comparingInt(County::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .map(Cluster::new)
                .collect(Collectors.toSet());

        for (int i = 0; i < iteractions; i++) {
            clusters.forEach(Cluster::cleanCounties);
            counties.forEach(county -> {
                Cluster nearestCluster = clusters.stream()
                        .min(Comparator.comparingDouble(x -> x.distance(county)))
                        .get();
                nearestCluster.insert(county);
            });

            clusters.forEach(Cluster::updateCentroid);
        }

        return clusters;
    }
}
