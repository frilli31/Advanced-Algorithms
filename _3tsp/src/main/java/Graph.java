public class Graph {
    final String name;
    final int[][] matrixOfDistances;

    public Graph(String n, int[][] matrix) {
        name = n;
        matrixOfDistances = matrix;
    }

    public int get(int x, int y) {
        return matrixOfDistances[x][y];
    }

    public int size() {
        return matrixOfDistances.length;
    }

    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append("Size: ").append(size());
        for(int[] row:matrixOfDistances) {
            representation.append('\n');
            for(int element: row)
                representation.append(element).append('\t');
        }
        return representation.toString();
    }
}
