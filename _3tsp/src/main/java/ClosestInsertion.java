import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClosestInsertion {
    private int size;
    private Graph graph;
    private Set<Integer> unvisitedNodes;
    private List<Integer> path;

    public ClosestInsertion(Graph graph) {
        this.graph = graph;
        this.size = graph.size();
    }

    public int calculatePathWeight() {
        init();

        while (!unvisitedNodes.isEmpty()) {
            int nodeToInsert = selection();
            insertion(nodeToInsert);
            unvisitedNodes.remove(nodeToInsert);
        }

        return lengthOfPath();
    }

    private void init() {
        unvisitedNodes = IntStream.range(1, size).boxed().collect(Collectors.toSet());
        path = new LinkedList<>();
        path.add(0);
        int bestNode = unvisitedNodes.stream().min(Comparator.comparingInt(x -> graph.get(0, x))).orElseThrow();

        path.add(bestNode);
        path.add(0);
        unvisitedNodes.remove(bestNode);
    }

    int selection() {
        int minDistance = Integer.MAX_VALUE;
        int k = 0;
        for(int i = 0; i < path.size(); i++) {
            int min = Integer.MAX_VALUE;
            int minNode = 0;
            int h = path.get(i);
            for(int j = 0; j < graph.size(); j++) {
                if(unvisitedNodes.contains(j) && graph.get(h, j) < min) {
                    min = graph.get(h, j);
                    minNode = j;
                }
            }

            if(min < minDistance) {
                minDistance = min;
                k = minNode;
            }
        }
        return k;
    }
/*
    int selection() {
        return unvisitedNodes.stream().min(Comparator.comparingInt(this::distanceFromPath)).orElseThrow();
    }

    int distanceFromPath(int node) {
        int n = path.stream().min(Comparator.comparingInt(h -> graph.get(h, node))).orElseThrow();
        System.out.println("N: " + n);
        return n;
    }
*/
    void insertion(int nodeToInsert) {
        int minWeight = Integer.MAX_VALUE;
        int positionOfMinimum = 0;

        ListIterator<Integer> pathIterator = path.listIterator();
        int source = pathIterator.next();

        while (pathIterator.hasNext()) {
            int destination = pathIterator.next();
            int weight = getInsertionWeight(nodeToInsert, source, destination);
            if (weight < minWeight) {
                minWeight = weight;
                positionOfMinimum = pathIterator.previousIndex();
            }
            source = destination;
        }

        path.add(positionOfMinimum, nodeToInsert);
    }



    int getInsertionWeight(int nodeToInsert, int source, int destination) {
        return graph.get(source, nodeToInsert) + graph.get(nodeToInsert, destination) - graph.get(source, destination);
    }

    int lengthOfPath() {
        int distance = 0;
        ListIterator<Integer> pathIterator = path.listIterator();

        int source = pathIterator.next();

        while (pathIterator.hasNext()) {
            int destination = pathIterator.next();
            distance += graph.get(source, destination);
            source = destination;
        }

        return distance;
    }
}
