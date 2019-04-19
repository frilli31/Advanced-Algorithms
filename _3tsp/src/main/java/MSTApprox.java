import java.util.function.IntSupplier;

public class MSTApprox implements IntSupplier {
    private Graph graph;
    private int size;

    public MSTApprox(Graph graph) {
        this.graph = graph;
        this.size = graph.size();
    }


    @Override
    public int getAsInt() {
        // TODO

        // TODO Prim Algorithm
        return  0;
    }
}
