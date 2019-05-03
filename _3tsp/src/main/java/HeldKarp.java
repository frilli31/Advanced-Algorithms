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

    private Map<Pair<Integer, Set<Integer>>, Integer> distances;
    private Set<Integer> wholeSet;

    public HeldKarp(Graph graph) {
        this.size = graph.size();
        this.graph = graph;
    }

    public int calculatePathWeightBitset() {
        final int lastElement = size - 1;

        Map<Pair<BitSet, Integer>, Integer> distanceContainer = new ConcurrentHashMap<>();

        IntStream.range(0, size - 1)
                .forEach(k -> distanceContainer.put(Pair.of(createBitSet(k), k), graph.get(k, lastElement)));

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Void> future = executor.submit(() -> {
            IntStream.range(2, size).forEach(lengthOfSubset -> {
                Combinations subsets = new Combinations(lastElement, lengthOfSubset);
                StreamSupport.stream(subsets.spliterator(), true).forEach(subset -> {
                    if (Thread.interrupted()) throw new RuntimeException();

                    BitSet myBitSet = createBitSet(subset);
                    for (int k : subset) {
                        BitSet previous = (BitSet) myBitSet.clone();
                        previous.set(k, false);
                        int min_distance = Integer.MAX_VALUE;
                        for (int iter : subset) {
                            if (k != iter) {
                                int distance = distanceContainer.get(Pair.of(previous, iter)) + graph.get(iter, k);
                                min_distance = distance < min_distance ? distance : min_distance;
                            }
                        }
                        distanceContainer.put(Pair.of(myBitSet, k), min_distance);
                    }
                });
            });

            return null;
        });

        try {
            future.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Timeout");
        } catch (Exception e) {
            System.out.println("Caught exception" + e.getCause());
        } finally {
            executor.shutdownNow();
        }

        BitSet allExceptLast = new BitSet(size);
        allExceptLast.flip(0, size - 1);

        return IntStream.range(0, lastElement).map(k -> {
            Pair<BitSet, Integer> pair = Pair.of(allExceptLast, k);
            return distanceContainer.containsKey(pair)
                ? distanceContainer.get(pair) + graph.get(k, lastElement)
                : -1;
        }).min().orElse(-1);
    }

    BitSet createBitSet(int index) {
        BitSet newSet = new BitSet(size);
        newSet.set(index);
        return newSet;
    }

    BitSet createBitSet(int[] indexes) {
        BitSet newSet = new BitSet(size);
        for (int index : indexes)
            newSet.set(index);
        return newSet;
    }

    public int calculatePathWeightDynamic() {
        wholeSet = IntStream.range(1, size).boxed().collect(Collectors.toSet());
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

        System.out.println(distances.size());

        return distances.getOrDefault(Pair.of(0, wholeSet), -1);
    }

    private int visit(Integer lastNode, Set<Integer> nodes, Integer distSoFar) {
        if (Thread.interrupted()) throw new RuntimeException();

        Pair<Integer, Set<Integer>> pair = Pair.of(lastNode, nodes);

        if (nodes.size() == 1 && nodes.contains(lastNode)) {
            // Save partial solution
            if (distSoFar < distances.getOrDefault(Pair.of(0, wholeSet), Integer.MAX_VALUE)) {
                distances.put(Pair.of(0, wholeSet), distSoFar);
            }

            return graph.get(lastNode, 0);
        }

        if (distances.get(pair) != null) {
            return distances.get(pair);
        }

        int mindist = Integer.MAX_VALUE;

        Set<Integer> notLastNodes = new HashSet<>(nodes);
        notLastNodes.remove(lastNode);

        for (Integer node : notLastNodes) {
            int distSet = visit(node, notLastNodes, distSoFar + graph.get(node, lastNode));
            int distFromLast = distSet + graph.get(node, lastNode);

            if (distFromLast < mindist) {
                mindist = distFromLast;
            }
        }

        distances.put(pair, mindist);

        return mindist;
    }
}
