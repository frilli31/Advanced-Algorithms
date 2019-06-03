import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<City> cities_original = new ArrayList<>(Parser.get("cities-and-towns-of-usa"));
        /*List<City> cities_250 = cities_original.stream()
                .filter(c -> c.getPopulation()>250)
                .collect(Collectors.toList());
        List<City> cities_2k = cities_250.stream()
                .filter(c -> c.getPopulation()>2_000)
                .collect(Collectors.toList());
        List<City> cities_5k = cities_2k.stream()
                .filter(c -> c.getPopulation()>5_000)
                .collect(Collectors.toList());
        List<City> cities_15k = cities_5k.stream()
                .filter(c -> c.getPopulation()>15_000)
                .collect(Collectors.toList());
        List<City> cities_50k = cities_15k.stream()
                .filter(c -> c.getPopulation()>50_000)
                .collect(Collectors.toList());
        List<City> cities_100k = cities_50k.stream()
                .filter(c -> c.getPopulation()>100_000)
                .collect(Collectors.toList());*/

        long fstTime = System.currentTimeMillis();
        Set<Cluster> kmeansClusters = ParallelKMeans.run(cities_original, 50, 10, 100000);
        System.out.println("Execution: " + (System.currentTimeMillis() - fstTime));

        kmeansClusters.stream().map(Cluster::toString).forEach(System.out::println);

        //Set<Cluster> kmeansClusters = SerialKMeans.run(counties_562, 15, 5);
        saveAsCSV(convertoToCSV(kmeansClusters), "latitude,longitude,population,cluster,centroid", "bubble-maps/kmeans.csv");
    }

    static double getDistortion(List<Cluster> clusters) {
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
            for (City county : cluster.cities) {
                String latitude = String.valueOf((int) county.getLat());
                String longitude = String.valueOf((int) county.getLon());
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
    }
}
