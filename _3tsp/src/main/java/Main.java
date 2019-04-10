public class Main {

    public static void main(String[] args) {
        Graph graph = GraphBuilder.get("tsp-dataset/ulysses22.tsp");

        graph = GraphBuilder.get("tsp-dataset/burma14.tsp");

        graph = GraphBuilder.get("tsp-dataset/eil51.tsp");

        System.out.println(graph);

        HeldKarp hk = new HeldKarp(graph);

        System.out.println(hk.call());
    }
}
