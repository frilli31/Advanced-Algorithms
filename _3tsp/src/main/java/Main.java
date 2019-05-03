import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        List<Graph> graphs = Stream.of("burma14", "ulysses22", "eil51", "kroD100", "gr229", "d493", "dsj1000")
                .map(GraphBuilder::get).collect(Collectors.toList());

        // System.out.println("\nHeldKarp");
        // graphs.forEach((graph) -> {
        //     HeldKarp solution = new HeldKarp(graph);
        //     long startTime = System.currentTimeMillis();
        //     int weight = solution.calculatePathWeightDynamic();
        //     long duration = System.currentTimeMillis() - startTime;

        //     System.out.println(graph.name + ": " + weight + " in " + duration + "ms");
        // });

        // System.out.println("\nClosestInsertion");
        // graphs.stream().forEach((graph) -> {
        //     ClosestInsertion heuristic = new ClosestInsertion(graph);
        //     long startTime = System.currentTimeMillis();
        //     int weight = heuristic.calculatePathWeight();
        //     long duration = System.currentTimeMillis() - startTime;

        //     System.out.println(graph.name + ": " + weight + " in " + duration + "ms");
        // });

        System.out.println("\nMST 2-approssimato");
        graphs.stream().forEach((graph) -> {
            MSTApprox mst = new MSTApprox(graph);
            long startTime = System.nanoTime();
            int weight = mst.primMST();
            long duration = System.nanoTime() - startTime;

            System.out.println(graph.name + ": " + weight + " in " + duration / 1000 + "micros");
        });
    }

    static String catchExecutionTime(IntSupplier function) {
        StringBuilder result = new StringBuilder("Result:\t");
        long startTime = System.currentTimeMillis();
        result.append(function.getAsInt()).append("\t\tExecution Time:\t")
                .append(System.currentTimeMillis() - startTime).append(" ms");
        return result.toString();
    }
}
