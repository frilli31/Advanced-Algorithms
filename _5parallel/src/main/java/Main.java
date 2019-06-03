import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<City> cities = new ArrayList<>(Parser.get("cities-and-towns-of-usa"));
        System.out.println(cities.size());

        ParallelKMeans.run(cities, 50, 100);


        //saveAsCSV(distortionCSV, "Clusters,Hierarchical,K-means", "distortion.csv");

        //Set<Cluster> kmeansClusters = SerialKMeans.run(counties_562, 15, 5);
        //saveAsCSV(convertoToCSV(kmeansClusters), "latitude,longitude,population,cluster,centroid", "bubble-maps/kmeans.csv");
    }

    static double getDistortion(Set<Cluster> clusters) {
        double distortion = clusters.stream().mapToDouble(Cluster::getError).sum();

        // Rounded to 4 significant digits
        BigDecimal bd = new BigDecimal(distortion);
        bd = bd.round(new MathContext(4));
        return bd.doubleValue();
    }

   /* static List<String[]> convertoToCSV(Set<Cluster> clusters) {
        List<String[]> dataLines = new ArrayList<>();

        int i = 0;

        for (Cluster cluster : clusters) {
            for (City county : cluster.cities) {
                String latitude = String.valueOf((int) county.getX());
                String longitude = String.valueOf((int) county.getY());
                String population = String.valueOf(county.getPopulation());
                String centroid = (int) cluster.centroid.getLatitude() + ";" + (int) cluster.centroid.getLongitude();

                dataLines.add(new String[]{latitude, longitude, population, String.valueOf(i), centroid});
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
    }*/
}
