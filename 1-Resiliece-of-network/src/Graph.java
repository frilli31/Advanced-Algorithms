import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Graph {
    Map<Integer, HashSet<Integer>> l;

    Graph() {
        l = new HashMap<>();
    }

    Graph(List<String> content) {
        this();
        content.stream()
                .filter(x -> !x.startsWith("#"))
                .map(x -> x.split("\t"))
                .forEach(x -> addArc(Integer.parseInt(x[0]), Integer.parseInt(x[1])));
    }

    @Override
    public Graph clone() {
        Graph new_graph = new Graph();
        new_graph.l = new HashMap<>(l.size());
        l.forEach((k,v) -> new_graph.l.put(k, new HashSet<>(v)));
        return new_graph;
    }

    void addNode(int label) {
        // if the node is already present It do nothing
        if (!l.containsKey(label))
            l.put(label, new HashSet<>());
    }

    private void disableNode(int n) {
        l.get(n).forEach(x->l.get(x).remove(n));
        l.remove(n);
    }

    void addArc(int source, int destination) {
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
        return l.values().parallelStream()
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
        return (double) l.values().parallelStream()
                .map(HashSet::size)
                .reduce(0, Integer::sum) / l.size();
    }

    public List<Integer> distributionOfDegree() {
        List<Integer> occurencies = l.values().parallelStream()
                .map(HashSet::size)
                .collect(Collectors.toList());

        return IntStream.rangeClosed(0, occurencies.stream().max(Comparator.naturalOrder()).orElse(0)).parallel()
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
        return connectedComponents().stream().mapToInt(Set::size).max().orElse(0);
    }

    boolean isResilient(double threshold) {
        return (double) resilience() / l.size() >= threshold;
    }

    boolean isResilient() {
        return isResilient(0.75);
    }

    void randomRemove(int n) {
        List<Integer> keys = new ArrayList<>(l.keySet());
        new Random().ints( 0, keys.size()).distinct().limit(n).map(keys::get).forEach(this::disableNode);
    }

    void bestNodeRemove(int n) {
        l.entrySet().stream()
                .sorted((x, y) -> Integer.compare(y.getValue().size(), x.getValue().size()))
                .limit(n)
                .map(Map.Entry::getKey)
                .forEach(this::disableNode);
    }

    List<Integer> resilienceAfterRemoveRandomRemove() {
        List<Integer> keys = new ArrayList<>(l.keySet());
        Collections.shuffle(keys);
        return keys.stream().peek(this::disableNode).map(x->resilience()).collect(Collectors.toList());
    }

    List<Integer> resilienceAfterBestNodeAttackRemove() {
        List<Integer> keys = l.entrySet().parallelStream()
                .sorted((x, y) -> Integer.compare(y.getValue().size(), x.getValue().size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return keys.stream().peek(this::disableNode).map(x->resilience()).collect(Collectors.toList());
    }
}

