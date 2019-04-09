import javafx.util.Pair;

import java.time.LocalTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Graph g = GraphBuilder.build("public_transport_dataset");

        System.out.println("Building graph: " + (System.currentTimeMillis() - startTime));

        execute_path(g, 500000079, 300000044, LocalTime.of(13, 00));

        execute_path(g, 200415016, 200405005, LocalTime.of(9, 30));

        execute_path(g, 300000032, 400000122, LocalTime.of(5, 30));

        execute_path(g, 210602003, 300000030, LocalTime.of(6, 30));

        execute_path(g, 200417051, 140701016, LocalTime.of(12, 00));

        execute_path(g, 200415009, 170402007, LocalTime.of(3, 0));

        execute_path(g, 221201005, 170402007, LocalTime.of(22, 0));

        execute_path(g, 300000032, 150606008, LocalTime.of(22, 0));

        execute_path(g, 170801002, 220402034, LocalTime.of(22, 0));
    }

    static void execute_path(Graph g, int source, int destination, LocalTime start_time) {
        long fstTime = System.currentTimeMillis();

        Pair<String, List<Integer>> fst = g.djkstraHeapSSSP(source, destination, start_time);
        System.out.println(fst.getKey());
        System.out.println("Execution with heap: " + (System.currentTimeMillis() - fstTime));

        long sndTime = System.currentTimeMillis();
        Pair<String, List<Integer>> snd = g.AStarSSSP(source, destination, start_time);
        System.out.println("Execution with A*: " + (System.currentTimeMillis() - sndTime));
        System.out.println("____________________________________________________________");

        new Chart(fst.getValue());
    }

}
