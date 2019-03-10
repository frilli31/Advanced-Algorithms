import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Main {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("as20000102.txt"), StandardCharsets.UTF_8);

        final int nodes = 6474;
        final double er_p = 0.0003;
        final int dpa_m = 2;
        final double percentage = 0.20;
        final int node_to_remove = (int) (percentage * nodes);
        List<Graph> graphs = List.of(new Graph(lines), new ER(nodes, er_p), new DPA(nodes, dpa_m));

        long start = System.currentTimeMillis();
        // Print which graph is resilient (maximum size connected component>75%) after removing 20% of node randomly
        System.out.println("Is the graph resilient after removing " + percentage * 100 + "% of nodes randomly");
        System.out.println("(The order is: Input graph, ER generated graph, DPA generated graph)");
        graphs.parallelStream().map(Graph::clone)
                .peek(x -> x.randomRemove(node_to_remove))
                .map(Graph::isResilient)
                .forEach(System.out::println);

        // Print which graph is resilient (maximum size connected component>75%) after removing the 20% of nodes
        // which have the maximum degree
        System.out.println("Is the graph resilient after removing " + percentage * 100 + "% of nodes by maximum degree");
        System.out.println("(The order is: Input graph, ER generated graph, DPA generated graph)");
        graphs.stream().map(Graph::clone) // Cannot use parallelStream because otherwise .limit() is not deterministic
                .peek(x -> x.bestNodeRemove(node_to_remove))
                .map(Graph::isResilient)
                .forEach(System.out::println);

        System.out.println("Now I' m going to do the attacks to all nodes and computate the resilience");
        // Execution of random random attack and best node attack
        List<List<Integer>> lists = graphs.parallelStream().map(Graph::clone)
                .map(Graph::resilienceAfterRemoveRandomRemove)
                .collect(Collectors.toList());
        List<List<Integer>> lists2 = graphs.parallelStream().map(Graph::clone)
                .map(Graph::resilienceAfterBestNodeAttackRemove)
                .collect(Collectors.toList());

        // creation of structure passed to the function that create and export the graph
        Map<String, List<Integer>> map = Map.of("Input Graph", lists.get(0),
                "ER with n=" + nodes + " p=" + er_p, lists.get(1),
                "DPA with n=" + nodes + " m=" + dpa_m, lists.get(2));
        Map<String, List<Integer>> map2 = Map.of("Input Graph", lists2.get(0),
                "ER with n=" + nodes + " p=" + er_p, lists2.get(1),
                "DPA with n=" + nodes + " m=" + dpa_m, lists2.get(2));
        // generation and save of OUTPUT graphs
        lineChart(map, "Random Attack");
        lineChart(map2, "Best Node Attack");
        System.out.println("The execution took "+ (double) (System.currentTimeMillis()-start)/1000 + " seconds");
    }

    private static void lineChart(Map<String, List<Integer>> map, String output_name) {
        String chartTitle = "Resilience after "+output_name;
        String xLabel = "Nodes Disabled";
        String yLabel = "Resilience";
        int chartDimensionX = 600;
        int chartDimensionY = 600;

        XYSeriesCollection seriesCollection = new XYSeriesCollection();

        map.forEach((k, list) -> {
            XYSeries series = new XYSeries(k);
            IntStream.range(0, list.size()).forEach(i -> series.add(i, list.get(i)));
            seriesCollection.addSeries(series);
        });

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xLabel, yLabel, seriesCollection, PlotOrientation.VERTICAL, true, true, false);

        try {
            ChartUtilities.saveChartAsJPEG(new File(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
                    + "_" + output_name + ".jpg"), chart, chartDimensionX, chartDimensionY);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }
}
