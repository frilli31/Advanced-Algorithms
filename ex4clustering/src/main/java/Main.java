import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Set<County> cities = Parser.get("unifiedCancerData_212");
        System.out.println(cities.size());

        System.out.println(HierarchicalClustering.run(cities, 15));

        System.out.println(ClusteringKMeans.run(cities, 15, 5));
    }
}
