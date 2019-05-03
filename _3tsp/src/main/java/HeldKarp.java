import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Combinations;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HeldKarp {
    private final int size;
    private final Graph graph;

    private Map<Pair<Integer, BitSet>, Integer> distances;
    private BitSet wholeSet;

    public HeldKarp(Graph graph) {
        this.size = graph.size();
        this.graph = graph;
    }

    public int calculatePathWeightDynamic() {
        wholeSet = new BitSet(size);
        distances = new HashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(() -> visit(0, wholeSet, 0));

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
        } catch (Exception e) {
            System.out.println("Caught exception" + e.getCause());
        } finally {
            executor.shutdownNow();
        }

        return distances.getOrDefault(Pair.of(0, wholeSet), -1);
    }

    private int visit(Integer lastNode, BitSet nodes, Integer distSoFar) {
        // System.out.println(lastNode + " " + nodes);
        if (Thread.interrupted()) throw new RuntimeException();

        Pair<Integer, BitSet> pair = Pair.of(lastNode, nodes);

        if (distances.get(pair) != null) {
            // System.out.println("Cache hit");
            return distances.get(pair);
        }

        if (nodes.cardinality() == size - 1 && !nodes.get(lastNode)) {
            // System.out.println("Caso base");
            Integer weight = graph.get(lastNode, 0);

            // Save partial solution
            if (distSoFar < distances.getOrDefault(Pair.of(0, wholeSet), Integer.MAX_VALUE)) {
                distances.put(Pair.of(0, wholeSet), distSoFar + weight);
            }

            // System.out.println("Partial solution " + distances.get(Pair.of(0, wholeSet)));

            return weight;
        }

        int mindist = Integer.MAX_VALUE;

        BitSet notLastNodes = (BitSet) nodes.clone();
        notLastNodes.set(lastNode);

        Integer node = notLastNodes.nextClearBit(0);

        while (node < size) {
            int distSet = visit(node, notLastNodes, distSoFar + graph.get(node, lastNode));
            int distFromLast = distSet + graph.get(node, lastNode);

            if (distFromLast < mindist) {
                mindist = distFromLast;
            }

            // System.out.println("mindist " + mindist);

            node = notLastNodes.nextClearBit(node + 1);
        }

        distances.put(pair, mindist);

        return mindist;
    }
}
