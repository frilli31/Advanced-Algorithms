import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.demo.charts.ExampleChart;
import java.io.IOException;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        // Set<County> cities_212 = Parser.get("unifiedCancerData_212");
        // Set<County> cities_562 = Parser.get("unifiedCancerData_562");
        Set<County> cities_1041 = Parser.get("unifiedCancerData_1041");
        // Set<County> cities_3108 = Parser.get("unifiedCancerData_3108");

        List<String[]> distortionCSV = IntStream.range(6, 21).mapToObj(numberOfClusters -> {
            Set<Cluster> hierarchicalClusters = HierarchicalClustering.run(cities_1041, numberOfClusters);
            Set<Cluster> kmeansClusters = ClusteringKMeans.run(cities_1041, numberOfClusters, 5);

            return new String[] {String.valueOf(numberOfClusters), String.valueOf(getDistortion(hierarchicalClusters)), String.valueOf(getDistortion(kmeansClusters))};
        }).collect(Collectors.toList());
        saveAsCSV(distortionCSV, "Clusters,Hierarchical,K-means", "distortion.csv");

        // Set<Cluster> hierarchicalClusters = HierarchicalClustering.run(cities_212, 15);
        // Set<Cluster> kmeansClusters = ClusteringKMeans.run(cities_212, 15, 5);
        // saveAsCSV(convertoToCSV(hierarchicalClusters), "x,y,population,cluster,centroid", "bubble-maps/hierarchical.csv");
        // saveAsCSV(convertoToCSV(kmeansClusters), "x,y,population,cluster,centroid", "bubble-maps/kmeans.csv");

        // ExampleChart<BubbleChart> bubbleChart1 = new BubbleChart01(hierarchicalClusters);
        // ExampleChart<BubbleChart> bubbleChart2 = new BubbleChart01(kmeansClusters);
        // BubbleChart chart1 = bubbleChart1.getChart();
        // BubbleChart chart2 = bubbleChart2.getChart();
        // try {
        //     BitmapEncoder.saveBitmapWithDPI(chart1, "./hierarchical", BitmapEncoder.BitmapFormat.JPG, 300);
        //     BitmapEncoder.saveBitmapWithDPI(chart2, "./kmeans", BitmapEncoder.BitmapFormat.JPG, 300);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    static double getDistortion(Set<Cluster> clusters) {
        double distortion = clusters.stream().mapToDouble(cluster -> cluster.getError()).sum();

        // Rounded to 4 significant digits
        BigDecimal bd = new BigDecimal(distortion);
        bd = bd.round(new MathContext(4));
        double rounded = bd.doubleValue();

        return rounded;
    }

    static List<String[]> convertoToCSV(Set<Cluster> clusters) {
        List<String[]> dataLines = new ArrayList<>();

        int i = 0;
        
        for (Cluster cluster : clusters) {
            for (County county : cluster.cities) {
                String x = String.valueOf((int) county.getX());
                String y = String.valueOf((int) county.getY());
                String population = String.valueOf((int) county.getPopulation());
                String centroid = (int) cluster.centroid.getX() + ";" + (int) cluster.centroid.getY();

                dataLines.add(new String[] { x, y, population, String.valueOf(i), centroid });
            }

            i++;
        }

        return dataLines;
    }

    static void saveAsCSV(List<String[]> dataLines, String header, String fileName) {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(header);

            dataLines.stream()
                .map(line -> String.join(",", line))
                .forEach(pw::println);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
