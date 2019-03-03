import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;
import java.text.SimpleDateFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Main {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("as20000102.txt"), StandardCharsets.UTF_8);
        final int nodes = 6474;
        final double er_p = 0.0003;
        final int dpa_m = 2;
        final double percentage = 0.20;
        final int node_to_remove = (int) (percentage * nodes);

        // Print which graph is resilient (maximum size connected component>75%) after removing 20% of node randomly
        System.out.println("Which graph is resilient after removing " + percentage * 100 + "% of nodes randomly");
        System.out.println("(The order is: Input graph, ER generated graph, DPA generated graph)");
        Stream.of(new Graph(lines), new ER(nodes, er_p), new DPA(nodes, dpa_m))
                .peek(x -> x.randomRemove(node_to_remove))
                .map(Graph::isResilient)
                .forEach(System.out::println);

        // Print which graph is resilient (maximum size connected component>75%) after removing the 20% of nodes
        // which have the maximum degree
        System.out.println("Which graph is resilient after removing " + percentage * 100 + "% of node which have the maximum degree");
        Stream.of(new Graph(lines), new ER(nodes, er_p), new DPA(nodes, dpa_m))
                .peek(x -> x.bestNodeRemove(node_to_remove))
                .map(Graph::isResilient)
                .forEach(System.out::println);

        List<List<Integer>> lists = Stream.of(new Graph(lines), new ER(nodes, er_p), new DPA(nodes, dpa_m))
                .map(x -> x.resilienceAfterRemove(Graph::randomRemove))
                .collect(Collectors.toList());
        Map<String, List<Integer>> map = Map.of("Input Graph", lists.get(0),
                "ER with n=" + nodes + " p=" + er_p, lists.get(1),
                "DPA with n=" + nodes + " m=" + dpa_m, lists.get(2));
        lineChart(map, "random");

        List<List<Integer>> lists2 = Stream.of(new Graph(lines), new ER(nodes, er_p), new DPA(nodes, dpa_m))
                .map(x -> x.resilienceAfterRemove(Graph::bestNodeRemove))
                .collect(Collectors.toList());
        Map<String, List<Integer>> map2 = Map.of("Input Graph", lists2.get(0),
                "ER with n=6474 p=0.0003", lists2.get(1),
                "DPA with n=6474 m=2", lists2.get(2));
        lineChart(map2, "best_node");
    }

    private static void lineChart(Map<String, List<Integer>> map, String output_name) {
        String chartTitle = "Resilience";
        String xLabel = "Nodes Disabled";
        String yLabel = "Resilience";
        int chartDimensionX = 1200;
        int chartDimensionY = 1200;

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
