import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParallelKMeans {
    static Set<Cluster> run(Set<City> counties, int number_of_centers, int iteractions) {
        List<Set<Cluster>> clusterings = new ArrayList<>();

        Set<Cluster> initial_clusters = counties.stream()
                .parallel()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())
                .limit(number_of_centers)
                .map(Centroid::new)
                .map(Cluster::new)
                .collect(Collectors.toSet());

        clusterings.add(initial_clusters);

        for (int i = 0; i < iteractions; i++) {
            Set<Cluster> my_clustering = clusterings.get(i);

            counties.forEach(city -> {
                Cluster nearestCluster = my_clustering.stream()
                        .min(Comparator.comparingDouble(x -> x.distance(city)))
                        .get();
                nearestCluster.insert(city);
            });

            clusterings.add(my_clustering.stream().map(Cluster::getCentroid).map(Cluster::new).collect(Collectors.toSet()));
        }
        clusterings.get(iteractions - 1).forEach(cluster -> cluster.centroid = cluster.getCentroid());
        return clusterings.get(iteractions - 1);
    }

    class Result {
        double x;
        double y;
        int size;

        Result() {
        }

        Result sum(Result r1, Result r2) {
            r1.x += r2.x;
            r1.y += r2.y;
            r1.size += r2.size;
            return r1;
        }
    }
}