import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Set<County> cities = Parser.get("unifiedCancerData_562");
        System.out.println(cities.size());

        //System.out.println(HierarchicalClustering.run(cities, 15));

        Set<Cluster> kmeans = ClusteringKMeans.run(cities, 15, 5);
        kmeans.forEach(cl -> System.out.println(cl.cities.size()));
    }
}
