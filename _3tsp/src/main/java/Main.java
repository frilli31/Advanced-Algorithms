import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        List<Graph> graphs = Stream.of("burma14", "ulysses22", "eil51", "kroD100", "gr229", "d493", "dsj1000")
                .map(GraphBuilder::get).collect(Collectors.toList());

        graphs.forEach((graph) -> {
            HeldKarp solution = new HeldKarp(graph);
            long startTime = System.currentTimeMillis();
            int weight = solution.calculatePathWeightBitset();
            long duration = System.currentTimeMillis() - startTime;

            System.out.println(graph.name + ": " + weight + " in " + duration + "ms");
        });

        graphs.stream().forEach((graph) -> {
            ClosestInsertion heuristic = new ClosestInsertion(graph);
            long startTime = System.currentTimeMillis();
            int weight = heuristic.calculatePathWeight();
            long duration = System.currentTimeMillis() - startTime;

            System.out.println(graph.name + ": " + weight + " in " + duration + "ms");
        });

        System.out.println("Algoritmo 2-approssimato");
        graphs.forEach(x -> new MSTApprox(x).primMST());
    }

    static String catchExecutionTime(IntSupplier function) {
        StringBuilder result = new StringBuilder("Result:\t");
        long startTime = System.currentTimeMillis();
        result.append(function.getAsInt()).append("\t\tExecution Time:\t")
                .append(System.currentTimeMillis() - startTime).append(" ms");
        return result.toString();
    }
}
