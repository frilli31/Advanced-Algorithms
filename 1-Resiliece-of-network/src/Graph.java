import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.*;


public class Graph {
    Map<Integer, HashSet<Integer>> l;

    Graph() {
        l = new HashMap<>();
    }

    public Graph(List<String> content) {
        this();
        content.stream()
                .filter(x -> !x.startsWith("#"))
                .map(x -> x.split("\t"))
                .forEach(x -> addArc(Integer.parseInt(x[0]), Integer.parseInt(x[1])));
    }

    void addNode(int label) {
        // if the node is already present It do nothing
        if (!l.containsKey(label))
            l.put(label, new HashSet<>());
    }

    private void disableNode(int n) {
        l.remove(n);
        l.values().forEach(x -> x.remove(n));
    }

    protected void addArc(int source, int destination) {
        if (source != destination) {
            // add the nodes (if they are not present
            addNode(source);
            addNode(destination);
            l.get(source).add(destination);
            l.get(destination).add(source);
        }
    }

    public String toString() {
        return l.keySet().stream()
                .map(k -> k + ": " + l.get(k).toString() + "\n")
                .reduce("", String::concat);
    }

    public int numberOfNodes() {
        return l.size();
    }

    public int numberOfArcs() {
        return l.values().stream()
                .map(HashSet::size)
                .reduce(0, Integer::sum) / 2;
    }

    public void printInfo() {
        System.out.println("Nodes: " + numberOfNodes() + "\tArcs: " + numberOfArcs() + "\tMedium Degree: " + mediumDegree());
    }

    public int degreeOfNode(int label) {
        return l.get(label).size();
    }

    public double mediumDegree() {
        return (double) l.values().stream()
                .map(HashSet::size)
                .reduce(0, Integer::sum) / l.size();
    }

    public List<Integer> distributionOfDegree() {
        List<Integer> occurencies = l.values().stream()
                .map(HashSet::size)
                .collect(Collectors.toList());

        return IntStream.range(0, occurencies.stream().max(Comparator.naturalOrder()).get())
                .map(x -> Collections.frequency(occurencies, x))
                .boxed()
                .collect(Collectors.toList());
    }

    private enum Color {white, grey, black}

    Set<Integer> DFSVisited(int u, Set<Integer> visited, Map<Integer, Color> color) {
        color.put(u, Color.grey);
        visited.add(u);
        for (int x : l.get(u))
            if (color.get(x) == Color.white)
                visited = DFSVisited(x, visited, color);
        color.put(u, Color.black);
        return visited;
    }

    Set<Set<Integer>> connectedComponents() {
        Map<Integer, Color> color = new HashMap<>();
        l.keySet().forEach(i -> color.put(i, Color.white));

        Set<Set<Integer>> cc = new HashSet<>();

        for (int vertex : l.keySet()) {
            if (color.get(vertex) == Color.white)
                cc.add(DFSVisited(vertex, new HashSet<>(), color));
        }
        return cc;
    }

    int resilience() {
        return connectedComponents().stream().mapToInt(Set::size).max().getAsInt();
    }

    boolean isResilient(double threshold) {
        return (double) resilience() / l.size() >= threshold;
    }

    boolean isResilient() {
        return isResilient(0.75);
    }

    void randomRemove(int n) {
        List<Integer> keys = new ArrayList<>(l.keySet());
        new Random().ints(n, 0, keys.size()).map(keys::get).forEach(this::disableNode);
    }

    List<Integer> resilienceAfterRemove(BiConsumer<Graph, Integer> policy) {
        List<Integer> list = new ArrayList<>(l.size());
        IntStream.range(0, l.size()).forEach(x -> {
            list.add(resilience());
            policy.accept(this, 1);
        });
        return list;
    }

    void bestNodeRemove(int n) {
        l.entrySet().stream()
                .sorted((x, y) -> Integer.compare(y.getValue().size(), x.getValue().size()))
                .map(Map.Entry::getKey)
                .limit(n)
                .forEach(this::disableNode);
    }
}

