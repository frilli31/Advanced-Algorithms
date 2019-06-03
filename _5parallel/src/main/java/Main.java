import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Set<City> counties_212 = Parser.get("unifiedCancerData_212");
        Set<City> cities = Parser.get("cities-and-towns-of-usa");
        System.out.println(cities.size());
        // Set<City> counties_1041 = Parser.get("unifiedCancerData_1041");
        // Set<City> counties_3108 = Parser.get("unifiedCancerData_3108");


        //saveAsCSV(distortionCSV, "Clusters,Hierarchical,K-means", "distortion.csv");

        //Set<Cluster> kmeansClusters = SerialKMeans.run(counties_562, 15, 5);
        //saveAsCSV(convertoToCSV(kmeansClusters), "x,y,population,cluster,centroid", "bubble-maps/kmeans.csv");
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
                String x = String.valueOf((int) county.getX());
                String y = String.valueOf((int) county.getY());
                String population = String.valueOf(county.getPopulation());
                String centroid = (int) cluster.centroid.getLatitude() + ";" + (int) cluster.centroid.getLongitude();

                dataLines.add(new String[]{x, y, population, String.valueOf(i), centroid});
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
