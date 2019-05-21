import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HierarchicalClustering {

    static Set<Cluster> run(Set<County> counties, int number_of_clusters) {
        int number_of_counties = counties.size();

        HashSet<Cluster> clusters = (HashSet) counties.stream()
                .map(Cluster::new)
                // .sorted(Comparator.comparingDouble(Cluster::getX))
                .collect(Collectors.toSet());

        while (clusters.size() > number_of_clusters) {
            Pair<Cluster, Cluster> closest_clusters = closestPair(clusters);
            clusters.remove(closest_clusters.getLeft());
            clusters.remove(closest_clusters.getRight());
            Cluster union = Cluster.union(closest_clusters.getLeft(), closest_clusters.getRight());
            clusters.add(union);
            System.out.println(clusters.size());
        }
        return clusters;
    }

    static Pair<Cluster, Cluster> closestPair(HashSet<Cluster> clusters) {
        /*return clusters.stream().map(cluster -> clusters.stream()
                                                    .map(cl2 -> Pair.of(cluster, cl2))
                                                    .filter(pair -> pair.getLeft() != pair.getRight())
                                            )
                .flatMap(x->x)
                .min(Comparator.comparingDouble(pair -> pair.getLeft().distance(pair.getRight())))
                .get();*/

        double min_distance = Double.MAX_VALUE;
        Pair<Cluster, Cluster> closest_pair = null;

        for (Cluster first : clusters)
            for (Cluster second : clusters)
                if (first != second && first.distance(second) < min_distance) {
                    min_distance = first.distance(second);
                    closest_pair = Pair.of(first, second);
                }
        return closest_pair;
    }
}
