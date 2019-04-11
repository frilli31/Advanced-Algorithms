import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Combinations;

import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class HeldKarp implements IntSupplier {
    private final int size;
    private final Graph graph;

    public HeldKarp(Graph graph) {
        this.size = graph.size();
        this.graph = graph;
    }

    @Override
    public int getAsInt() {
        final int lastElement = size - 1;

        Map<Pair<BitSet, Integer>, Integer> distanceContainer = new ConcurrentHashMap<>();

        IntStream.range(0, size - 1).forEach(k ->
                distanceContainer.put(Pair.of(createBitSet(k), k), graph.get(k, lastElement))
        );

        IntStream.range(2, size).forEach(lengthOfSubset -> {
            Combinations subsets = new Combinations(lastElement, lengthOfSubset);
            //System.out.println("Considering combination of size " + lengthOfSubset);
            StreamSupport.stream(subsets.spliterator(), true).forEach(subset -> {
                if (Thread.interrupted())
                    throw new RuntimeException();
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

        BitSet allExceptLast = new BitSet(size);
        allExceptLast.flip(0, size - 1);

        return IntStream.range(0, lastElement).map(k -> distanceContainer.get(Pair.of(allExceptLast, k)) + graph.get(k, lastElement)).min().orElseThrow();
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
}
