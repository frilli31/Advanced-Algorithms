import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSTApprox {
    private int[][] graph;
    private int size;

    public MSTApprox(Graph graph) {
        this.graph = graph.matrixOfDistances;
        this.size = graph.size();
    }

    int treeBuilder(Integer parent[], int graph[][]) {
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < this.size; i++) {
            nodes.add(new Node(""+i, ""+i, ""+parent[i]));
        }

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
        Integer key[] = new Integer[size];
        Integer pi[] = new Integer[size];
        Boolean mstSet[] = new Boolean[this.size];
        Map<Integer, Integer> distance = new HashMap<>();

        for(int i=0; i<size; i++) {
            key[i] = Integer.MAX_VALUE;
            pi[i] = null;
            mstSet[i] = false;
            distance.put(i, graph[0][i]);
        }

        key[0] = 0;
        MinHeap q = new MinHeap(distance);

        for(int i = 0; i< size; i++) {
            q.add(i);
        }
        while(q.size() != 0) {
            int u = q.extractMin();
            mstSet[u] = true;
            for(int i = 0; i < size; i++) {
                if (this.graph[u][i]!= 0 && !mstSet[i] && this.graph[u][i] < key[i]) {
                    pi[i] = u;
                    key[i] = this.graph[u][i];
                    q.decreaseKey(i, key[i]);
                }
            }
        }

        return treeBuilder(pi, graph);
    }
}

/*
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

 */
