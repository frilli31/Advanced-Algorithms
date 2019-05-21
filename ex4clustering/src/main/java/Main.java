import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Set<County> cities = Parser.get("unifiedCancerData_3108");
        System.out.println(cities.size());

        System.out.println(HierarchicalClustering.run(cities, 15));
    }
}
