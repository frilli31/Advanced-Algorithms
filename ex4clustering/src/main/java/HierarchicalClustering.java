import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HierarchicalClustering {

    static Set<Cluster> run(Set<County> counties, int number_of_clusters) {
        ArrayList<Cluster> clusters_by_x = (ArrayList<Cluster>) counties.stream().map(Cluster::new)
                .sorted(Comparator.comparingDouble(Cluster::getX)).collect(Collectors.toList());

        ArrayList<Cluster> clusters_by_y = (ArrayList<Cluster>) clusters_by_x.stream()
                .sorted(Comparator.comparingDouble(Cluster::getY)).collect(Collectors.toList());

        while (clusters_by_x.size() > number_of_clusters) {
            Result closest_clusters = fastClosestPair(clusters_by_x, clusters_by_y);

            Cluster first = closest_clusters.first;
            Cluster second = closest_clusters.second;
            Set<Cluster> cl_to_remove = Set.of(first, second);

            clusters_by_x.removeAll(cl_to_remove);
            clusters_by_y.removeAll(cl_to_remove);

            Cluster union = Cluster.union(first, second);

            int index_of_x = 0;
            int index_of_y = 0;

            int size = clusters_by_x.size();
            for (int i = 0; i < size; i++) {
                if (union.getX() > clusters_by_x.get(i).getX())
                    index_of_x++;
                if (union.getY() > clusters_by_y.get(i).getY())
                    index_of_y++;
            }
            clusters_by_x.add(index_of_x, union);
            clusters_by_y.add(index_of_y, union);
        }
        return new HashSet<>(clusters_by_x);
    }

    static class Result {
        double distance;
        Cluster first;
        Cluster second;

        public Result(double distance, Cluster first, Cluster second) {
            this.distance = distance;
            this.first = first;
            this.second = second;
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

            Result closestLeft = fastClosestPair(first_half, arrays_splitted.getLeft());
            Result closestRight = fastClosestPair(second_half, arrays_splitted.getRight());

            Result closestLR = closestLeft.distance < closestRight.distance ? closestLeft : closestRight;

            double mid = ((clusters_by_x.get(half_index).centroid.getX())
                    + clusters_by_x.get(half_index + 1).centroid.getX()) / 2;

            Result closestMid = closestPairStrip(clusters_by_y, mid, closestLR.distance);

            return closestMid.distance <= closestLR.distance ? closestMid : closestLR;
        }
    }

    static Pair<List<Cluster>, List<Cluster>> split(List<Cluster> cluster_by_y, List<Cluster> first_half,
            List<Cluster> second_half) {
        int size = cluster_by_y.size();

        ArrayList<Cluster> first = new ArrayList<>(size / 2 + 1);
        ArrayList<Cluster> second = new ArrayList<>(size / 2 + 1);

        cluster_by_y.forEach(cluster -> {
            if (first_half.contains(cluster)) first.add(cluster);
            else second.add(cluster);
        });
        return Pair.of(first, second);
    }

    static Result closestPairStrip(List<Cluster> clusters_by_y, double mid, double distance) {
        int n = clusters_by_y.size();
        List<Cluster> s1 = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (Math.abs(clusters_by_y.get(i).getX() - mid) < distance) {
                s1.add(clusters_by_y.get(i));
                indexes.add(i);
            }
        }

        Result r = new Result(Double.MAX_VALUE, null, null);

        for (int u = 0; u < s1.size() - 1; u++) {
            Cluster c1 = s1.get(u);
            int min;
            if (u + 5 < s1.size() - 1)
                min = u + 5;
            else
                min = s1.size() - 1;
            for (int v = u + 1; v <= min; v++) {
                Cluster c2 = s1.get(v);
                double d = c1.distance(c2);
                if (d < r.distance) {
                    r = new Result(d, c1, c2);
                }
            }
        }
        return r;
    }

    static Result slowClosestPair(List<Cluster> clusters) {
        Result result = new Result(Double.POSITIVE_INFINITY, null, null);
        int size = clusters.size();

        for (int i = 0; i < size - 1; i++) {
            Cluster first = clusters.get(i);
            for (int j = i + 1; j < size; j++) {
                Cluster second = clusters.get(j);
                double distance = first.distance(second);
                if (distance < result.distance) {
                    result = new Result(distance, first, second);
                }
            }
        }

        return result;
    }
}
