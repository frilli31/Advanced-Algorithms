import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Graph burma14 = GraphBuilder.get("tsp-dataset/burma14.tsp");
        Graph ulyssses22 = GraphBuilder.get("tsp-dataset/ulysses22.tsp");
        Graph eil51 = GraphBuilder.get("tsp-dataset/eil51.tsp");

        timeLimitedHeldKarp(eil51);
    }

    static void timeLimitedHeldKarp(Graph graph) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        TimeLimiter timeLimiter = SimpleTimeLimiter.create(threadPool);
        HeldKarp target = new HeldKarp(graph);

        int timeBoundInms = 500;
        Callable<Integer> proxy = timeLimiter.newProxy(target, Callable.class, timeBoundInms, TimeUnit.MILLISECONDS);

        try {
            long startTime = System.currentTimeMillis();
            System.out.println("The result is: " + proxy.call());
            System.out.println("The execution took: " + (System.currentTimeMillis() - startTime) + " ms");
        } catch (Exception e) {
            System.out.println("The execution took longer then " + timeBoundInms + " ms");
        } finally {
            threadPool.shutdownNow();
        }
    }
}
