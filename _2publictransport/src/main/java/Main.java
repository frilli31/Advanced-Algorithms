import javafx.util.Pair;

import java.time.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        Graph g = GraphBuilder.build("public_transport_dataset");

        System.out.println("Building graph: " + (System.nanoTime()-startTime)/1000000);

        execute_path(g, 500000079 ,300000044, 1300);

        execute_path(g,200415016   ,200405005, 930);

        execute_path(g,300000032  ,400000122, 530);

        execute_path(g, 210602003  ,300000030, 630);

        execute_path(g, 200417051  ,140701016, 1200);

        execute_path(g, 200417051  ,140701016, 2355);
    }

    static void execute_path(Graph g, int source, int destination, int start_time) {
        long startTime = System.nanoTime();

        Pair<String, List<Integer>> first = g.calculateShortestPathFromSource(source ,destination, start_time);
        System.out.println(first.getKey());
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);
        new Chart(first.getValue());
    }

}
