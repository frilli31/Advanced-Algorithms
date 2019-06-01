import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.demo.charts.ExampleChart;
import java.io.IOException;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        //Set<County> cities_212 = Parser.get("unifiedCancerData_212");
        // Set<County> cities_562 = Parser.get("unifiedCancerData_562");
        //Set<County> cities_1041 = Parser.get("unifiedCancerData_1041");
        Set<County> cities_3108 = Parser.get("unifiedCancerData_3108");

        long hierarchicalTime = System.currentTimeMillis();
        Set<Cluster> hierarchicalClusters = HierarchicalClustering.run(cities_3108, 15);
        System.out.println("HierarchicalClustering in " + (System.currentTimeMillis() - hierarchicalTime) + "ms");

        long kmeansTime = System.currentTimeMillis();
        Set<Cluster> kmeansClusters = ClusteringKMeans.run(cities_3108, 15, 5);
        System.out.println("K-Means in " + (System.currentTimeMillis() - kmeansTime) + "ms");

        saveAsCSV(hierarchicalClusters, "bubble-maps/hierarchical.csv");
        saveAsCSV(kmeansClusters, "bubble-maps/kmeans.csv");

        // ExampleChart<BubbleChart> bubbleChart = new BubbleChart01(hierarchicalClusters);
        // BubbleChart chart = bubbleChart.getChart();
        // try {
        //     BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.JPG, 300);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    static void saveAsCSV(Set<Cluster> clusters, String fileName) {
        List<String[]> dataLines = new ArrayList<>();

        int i = 0;
        
        for (Cluster cluster : clusters) {
            for (County county : cluster.cities) {
                dataLines.add(new String[] 
                { String.valueOf(county.getX()), String.valueOf(county.getY()), String.valueOf(county.getPopulation()), String.valueOf(i) });
            }

            i++;
        }

        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("x,y,population,cluster");

            dataLines.stream()
                .map(line -> String.join(",", line))
                .forEach(pw::println);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
