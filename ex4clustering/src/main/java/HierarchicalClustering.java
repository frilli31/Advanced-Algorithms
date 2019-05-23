import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HierarchicalClustering {

    static Set<Cluster> run(Set<County> counties, int number_of_clusters) {
        ArrayList<Cluster> clusters_by_x = (ArrayList<Cluster>) counties.stream()
                .map(Cluster::new)
                .sorted(Comparator.comparingDouble(Cluster::getX))
                .collect(Collectors.toList());

        ArrayList<Cluster> clusters_by_y = (ArrayList<Cluster>) clusters_by_x.stream()
                .sorted(Comparator.comparingDouble(Cluster::getY))
                .collect(Collectors.toList());

        while (clusters_by_x.size() > number_of_clusters) {

            // ciclo per controllare che effettivamente i valori siano nel corretto ordine (da eliminare in futuro)
            for(int i=0; i<clusters_by_x.size()-1; i++) {
                assert clusters_by_x.get(i).getX() < clusters_by_x.get(i+1).getX();
                assert clusters_by_y.get(i).getY() < clusters_by_y.get(i+1).getY();
            }

            Result closest_clusters = fastClosestPair(clusters_by_x, clusters_by_y);

            Cluster first = clusters_by_y.get(closest_clusters.index_of_first);
            Cluster second = clusters_by_y.get(closest_clusters.index_of_second);
            Set<Cluster> cl_to_remove = Stream.of(first, second).collect(Collectors.toSet());

            clusters_by_x.removeAll(cl_to_remove);
            clusters_by_y.removeAll(cl_to_remove);

            Cluster union = Cluster.union(first, second);

            int index_of_x = 0;
            int index_of_y = 0;

            int size = clusters_by_x.size();
            for(int i=0; i<size; i++) {
                if(union.getX()>clusters_by_x.get(i).getX())
                    index_of_x++;
                if(union.getY()>clusters_by_y.get(i).getY())
                    index_of_y++;
            }
            clusters_by_x.add(index_of_x, union);
            clusters_by_y.add(index_of_y, union);
        }
        return new HashSet<>(clusters_by_x);
    }

    static class Result {
        double distance;
        int index_of_first;
        int index_of_second;

        public Result(double distance, int index_of_first, int index_of_second) {
            this.distance = distance;
            this.index_of_first = index_of_first;
            this.index_of_second = index_of_second;
        }
    }

    static Result fastClosestPair(List<Cluster> clusters_by_x, List<Cluster> clusters_by_y) {
        int size = clusters_by_x.size();

        if (size <= 3)
            return slowClosestPair(clusters_by_x);
        else {
            int half_index = clusters_by_x.size() / 2;
            List<Cluster> first_half = clusters_by_x.subList(0, half_index);
            List<Cluster> second_half = clusters_by_x.subList(half_index, clusters_by_x.size());

            Pair<List<Cluster>, List<Cluster>> arrays_splitted = split(clusters_by_y, first_half, second_half);

            Result result = Stream.of(
                        Pair.of(first_half, arrays_splitted.getLeft()),
                        Pair.of(second_half, arrays_splitted.getRight())
                    )
                    //.parallel()
                    .map(p -> fastClosestPair(p.getLeft(), p.getRight()))
                    .min(Comparator.comparingDouble(ris -> ris.distance))
                    .get();

            double mid = ((clusters_by_x.get(half_index).centroid.getX()) + clusters_by_x.get(half_index+1).centroid.getX()) / 2;

            Result r2 = closestPairStrip(clusters_by_y, mid, result.distance);

            if(result.distance <= r2.distance)
                return result;
            else
                return r2;
        }
    }

    static Pair<List<Cluster>, List<Cluster>> split(List<Cluster> cluster_by_y, List<Cluster> first_half, List<Cluster> second_half) {
        int size = cluster_by_y.size();

        ArrayList<Cluster> first = new ArrayList<>(size / 2 + 1);
        ArrayList<Cluster> second = new ArrayList<>(size / 2 + 1);

        cluster_by_y.forEach(cluster -> {
            if (first_half.contains(cluster))
                first.add(cluster);
            else
                second.add(cluster);
        });
        return Pair.of(first, second);
    }

    static Result closestPairStrip(List<Cluster> clusters_by_y, double mid, double distance) {
        int n = clusters_by_y.size();
        ArrayList<Cluster> a = new ArrayList<>();

        Result min = new Result(Double.MAX_VALUE, 0, 0);  // Initialize the minimum distance as d

        for (int i = 0; i < n; ++i) {
            Cluster cluster_i = clusters_by_y.get(i);
            for (int j = i + 1; j < n && (clusters_by_y.get(j).getY() - cluster_i.getY()) < distance; ++j) {
                Cluster cluster_j = clusters_by_y.get(j);
                double d = cluster_i.distance(cluster_j);
                if (d < min.distance)
                    min = new Result(d, i, j);
            }
        }
        assert min.distance != Double.MAX_VALUE;
        return min;
    }


    static Result slowClosestPair(List<Cluster> clusters) {
        Result result = new Result(Double.POSITIVE_INFINITY, 0, 0);
        int size = clusters.size();
        assert size > 1;

        for (int i = 0; i < size-1; i++) {
            Cluster first = clusters.get(i);
            for (int j = i+1; j < size; j++) {
                    Cluster second = clusters.get(j);
                    double distance = first.distance(second);
                    if (distance < result.distance) {
                        result = new Result(distance, i, j);
                    }
            }
        }
        assert result.distance != Double.POSITIVE_INFINITY;
        return result;
    }
}
