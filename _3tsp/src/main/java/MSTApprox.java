import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

// TODO Heap di Fibonacci per complessità O(|E| + |V|log|V|)

public class MSTApprox {
    private int[][] graph;
    private int size;

    public MSTApprox(Graph graph) {
        this.graph = graph.matrixOfDistances;
        this.size = graph.size();
    }

/*
    @Override
    public int getAsInt() {
        for(int i = 0; i < size; i++) {

        }
        return  0;
    }
*/
    int minKey(int key[], Boolean mstSet[]) {
        int min = Integer.MAX_VALUE;
        int min_index=-1;

        for (int v = 0; v < this.size; v++)
            if (mstSet[v] == false && key[v] < min) {
                min = key[v];
                min_index = v;
            }

        return min_index;
    }

    int treeBuilder(int parent[], int graph[][]) {

        List<Node> nodes = new ArrayList<>();

        for(int i = 0; i < this.size; i++) {
            nodes.add(new Node(""+i, ""+i, ""+parent[i]));
        }

        /*
        System.out.println("Edge \tWeight");
        for (int i = 1; i < this.size; i++) {
            System.out.println(parent[i]+" - "+ i+"\t"+ graph[i][parent[i]]);
        }
        */

        Node root = GeneralTree.createTree(nodes);
        List<Node> deepPreorderList = GeneralTree.flatten(root);

        int totalWeight = 0;
        for(int i = 0; i < deepPreorderList.size(); i++) {
            int nodeA = Integer.parseInt(deepPreorderList.get(i).getId());
            int nodeB;
            if(i == deepPreorderList.size() - 1)
                nodeB = Integer.parseInt(deepPreorderList.get(0).getId());
            else
                nodeB = Integer.parseInt(deepPreorderList.get(i + 1).getId());
            totalWeight = totalWeight + graph[nodeA][nodeB];
        }

        return totalWeight;
    }

    int primMST() {
        int parent[] = new int[this.size];
        int key[] = new int [this.size];
        Boolean mstSet[] = new Boolean[this.size];

        for (int i = 0; i < this.size; i++) {
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }

        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < this.size - 1; count++) {
            int u = minKey(key, mstSet);
            mstSet[u] = true;

            for (int v = 0; v < this.size; v++)
                if (this.graph[u][v]!= 0 && !mstSet[v] && this.graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = this.graph[u][v];
                }
        }

        return treeBuilder(parent, graph);
    }
}
