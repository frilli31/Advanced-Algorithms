import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        List<Graph> graphs = Stream.of("burma14", "ulysses22", "eil51", "kroD100", "gr229", "d493").map(GraphBuilder::get).collect(Collectors.toList());

        graphs.forEach(Main::timeLimitedHeldKarp);

        graphs.stream().forEach((graph) -> {
            ClosestInsertion heuristic = new ClosestInsertion(graph);
            long startTime = System.currentTimeMillis();
            int weight = heuristic.calculateWeight();
            long duration = System.currentTimeMillis() - startTime;

            System.out.println(graph.name + ": " + weight + " in " + duration + "ms");
        });

        System.out.println("Algoritmo 2-approssimato");
        graphs.forEach(x -> new MSTApprox(x).primMST());
    }

    static void timeLimitedHeldKarp(Graph graph) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        TimeLimiter timeLimiter = SimpleTimeLimiter.create(threadPool);
        HeldKarp target = new HeldKarp(graph);

        int timeBoundInms = 500;
        IntSupplier proxy = timeLimiter.newProxy(target, IntSupplier.class, timeBoundInms, TimeUnit.MILLISECONDS);

        try {
            System.out.println(catchExecutionTime(proxy));
        } catch (Exception e) {
            System.out.println("The execution took longer then " + timeBoundInms + " ms");
        } finally {
            threadPool.shutdownNow();
        }
    }

    static String catchExecutionTime(IntSupplier function) {
        StringBuilder result = new StringBuilder("Result:\t");
        long startTime = System.currentTimeMillis();
        result.append(function.getAsInt()).append("\t\tExecution Time:\t")
                .append(System.currentTimeMillis() - startTime).append(" ms");
        return result.toString();
    }
}
