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
        // Set<County> counties_212 = Parser.get("unifiedCancerData_212");
        Set<County> counties_562 = Parser.get("unifiedCancerData_562");
        // Set<County> counties_1041 = Parser.get("unifiedCancerData_1041");
        // Set<County> counties_3108 = Parser.get("unifiedCancerData_3108");

        List<String[]> distortionCSV = IntStream.range(6, 21).mapToObj(numberOfClusters -> {
            Set<Cluster> hierarchicalClusters = HierarchicalClustering.run(counties_562, numberOfClusters);
            Set<Cluster> kmeansClusters = ClusteringKMeans.run(counties_562, numberOfClusters, 5);

            return new String[] {String.valueOf(numberOfClusters), String.valueOf(getDistortion(hierarchicalClusters)), String.valueOf(getDistortion(kmeansClusters))};
        }).collect(Collectors.toList());
        saveAsCSV(distortionCSV, "Clusters,Hierarchical,K-means", "distortion.csv");

        Set<Cluster> hierarchicalClusters = HierarchicalClustering.run(counties_562, 15);
        Set<Cluster> kmeansClusters = ClusteringKMeans.run(counties_562, 15, 5);
        saveAsCSV(convertoToCSV(hierarchicalClusters), "x,y,population,cluster,centroid", "bubble-maps/hierarchical.csv");
        saveAsCSV(convertoToCSV(kmeansClusters), "x,y,population,cluster,centroid", "bubble-maps/kmeans.csv");
    }

    static double getDistortion(Set<Cluster> clusters) {
        double distortion = clusters.stream().mapToDouble(Cluster::getError).sum();

        // Rounded to 4 significant digits
        BigDecimal bd = new BigDecimal(distortion);
        bd = bd.round(new MathContext(4));
        return bd.doubleValue();
    }

    static List<String[]> convertoToCSV(Set<Cluster> clusters) {
        List<String[]> dataLines = new ArrayList<>();

        int i = 0;
        
        for (Cluster cluster : clusters) {
            for (County county : cluster.counties) {
                String x = String.valueOf((int) county.getX());
                String y = String.valueOf((int) county.getY());
                String population = String.valueOf(county.getPopulation());
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
