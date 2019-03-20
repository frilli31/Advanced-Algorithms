import java.time.*;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        Graph g = GraphBuilder.build("public_transport_dataset");
        Chart map = new Chart(g.stations);
        g.set_map(map);

        System.out.println("Building graph: " + (System.nanoTime()-startTime)/1000000);
        startTime = System.nanoTime();

        System.out.println(g.calculateShortestPathFromSource(500000079 ,300000044, 1300));
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);
        startTime = System.nanoTime();

        System.out.println(g.calculateShortestPathFromSource(200415016   ,200405005, 930));
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);
        startTime = System.nanoTime();

        System.out.println(g.calculateShortestPathFromSource(300000032  ,400000122, 530));
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);
        startTime = System.nanoTime();

        System.out.println(g.calculateShortestPathFromSource(210602003  ,300000030, 630));
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);
        startTime = System.nanoTime();

        System.out.println(g.calculateShortestPathFromSource(200417051  ,140701016, 1200));
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);
        startTime = System.nanoTime();

        System.out.println(g.calculateShortestPathFromSource(200417051  ,140701016, 2355));
        System.out.println("Execution : " + (System.nanoTime()-startTime)/1000000);

        System.out.println("Wait for the chart...");
        map.export_chart();
        System.out.println("DONE!");
    }

}
