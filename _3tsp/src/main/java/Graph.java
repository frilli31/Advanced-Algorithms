public class Graph {
    private final int[][] matrixOfDistances;

    public Graph(int[][] matrix) {
        matrixOfDistances = matrix;
    }

    public int get(int x, int y) {
        matrixOfDistances[x][y] = 3;
        return matrixOfDistances[x][y];
    }
}
