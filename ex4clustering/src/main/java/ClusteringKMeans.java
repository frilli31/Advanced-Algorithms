import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClusteringKMeans {
    static Set<Cluster> run(Set<County> counties, int number_of_centers, int iteractions) {
        List<Set<Cluster>> clusterings = new ArrayList<>();

        Set<Cluster> initial_clusters = counties.stream()
                .sorted(Comparator.comparingInt(County::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .map(Cluster::new)
                .collect(Collectors.toSet());

        clusterings.add(initial_clusters);

        for (int i = 0; i < iteractions; i++) {
            Set<Cluster> my_clustering = clusterings.get(i);

            counties.forEach(county -> {
                Cluster nearestCluster = my_clustering.stream()
                        .min(Comparator.comparingDouble(x -> x.distance(county)))
                        .get();
                nearestCluster.insert(county);
            });

            clusterings.add(my_clustering.stream().map(Cluster::getCentroid).map(Cluster::new).collect(Collectors.toSet()));
        }
        clusterings.get(iteractions - 1).forEach(cluster -> cluster.centroid = cluster.getCentroid());
        return clusterings.get(iteractions-1);
    }
}
