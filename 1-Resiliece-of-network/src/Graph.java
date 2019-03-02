import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Graph {
    protected Map<Integer, HashSet<Integer>> l;

    protected Graph() {
        l=new HashMap<>();
    }

    public Graph(List<String> content) {
        this();
        content.stream()
                .filter(x->!x.startsWith("#"))
                .map(x->x.split("\t"))
                .forEach(x-> addArc(Integer.parseInt(x[0]), Integer.parseInt(x[1])));
    }

    protected void addNode(int label) {
        // if the node is already present It do nothing
        if(!l.containsKey(label))
            l.put(label, new HashSet<>());
    }

    protected void addArc(int source, int destination) {
        if(source != destination) {
            // add the nodes (if they are not present
            addNode(source);
            addNode(destination);
            l.get(source).add(destination);
            l.get(destination).add(source);
        }
    }

    public String toString() {
        return l.keySet().stream()
                .map(k-> k+": "+l.get(k).toString()+"\n")
                .reduce("", String::concat);
    }

    public int numberOfNodes() {
        return l.keySet().size();
    }

    public int numberOfArcs() {
        return l.values().stream()
                .map(HashSet::size)
                .reduce(0, Integer::sum)/2;
    }

    public void printInfo() {
        System.out.println("Nodes: "+numberOfNodes()+"\tArcs: "+numberOfArcs()+"\tMedium Degree: "+mediumDegree());
    }

    public int degreeOfNode(int label) {
        return l.get(label).size();
    }

    public double mediumDegree() {
        return (double) l.values().stream()
                .map(HashSet::size)
                .reduce(Integer::sum)
                .get() / l.size();
    }

    public List<Integer> distributionOfDegree() {
        List<Integer> occurencies = l.values().parallelStream()
                .map(HashSet::size)
                .collect(Collectors.toList());

        return IntStream.range(0, occurencies.stream().max(Comparator.naturalOrder()).get())
                .map(x->Collections.frequency(occurencies, x))
                .boxed()
                .collect(Collectors.toList());
    }

    public void disableNode(int n) {
        l.remove(n);
        l.values().forEach(x->x.remove(n));
    }

    public static final int white = 0;
    public static final int grey = 1;
    public static final int black = 2;
    public enum Color { white, grey, black; };

    Set<Integer> DFSVisited(int u, Set<Integer> visited, Map<Integer, Color> color) {
        color.put(u, Color.grey);
        visited.add(u);
        for(int x: l.get(u))
            if(color.get(x) == Color.white)
                visited = DFSVisited(x, visited, color);
        color.put(u, Color.black);
        return visited;
    }

    Set<Set<Integer>> connectedComponents() {
        Map<Integer, Color> color = new HashMap<>();
        l.keySet().forEach(i->color.put(i, Color.white));

        Set<Set<Integer>> cc = new HashSet<>();

        for(int vertex: l.keySet()) {
            if(color.get(vertex) == Color.white)
                cc.add(DFSVisited(vertex, new HashSet<>(), color));
        }
        return cc;
    }

    boolean isConnected() {
        return connectedComponents().size() == 1;
    }

    double resilience() {
        return (double) connectedComponents().stream().mapToInt(Set::size).max().getAsInt() / l.size();
    }

    boolean isResilient(double threshold) {
        return resilience()>=threshold;
    }
    boolean isResilient() {
        return isResilient(0.75);
    }

    void randomRemove(double percentage) {
        Random rn = new Random();
        List<Integer> keys = new ArrayList<>(l.keySet());
        Collections.shuffle(keys);
        keys.stream().limit((int) (l.size()*percentage/100)).forEach(this::disableNode);
    }

    void bestNodeRemove(int n) {
        l.entrySet().stream()
                .sorted((x, y)-> Integer.compare(y.getValue().size(), x.getValue().size()))
                .map(Map.Entry::getKey)
                .limit(n)
                .forEach(this::disableNode);
    }

}

