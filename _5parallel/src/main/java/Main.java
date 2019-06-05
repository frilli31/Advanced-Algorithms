import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.demo.charts.ExampleChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<City> cities_original = new ArrayList<>(Parser.get("cities-and-towns-of-usa"));
        System.out.println(cities_original.size());

        // ANSWER 1
        {
            List<Double> populations = new ArrayList<>();
            List<Double> serial_values = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            List<Integer> filters = Arrays.asList(100_000, 50_000, 15_000, 5_000, 2_000, 250, Integer.MIN_VALUE);

            filters.forEach(filter -> {
                List<City> cities = cities_original.stream()
                        .filter(c -> c.getPopulation() > filter)
                        .collect(Collectors.toList());
                System.out.println("Doing " + filter + " Size is " + cities.size());

                populations.add((double) cities.size());

                long fstTime = System.currentTimeMillis();
                Set<Cluster> serial_kmeans_clusters = SerialKMeans.run(cities, 50, 100);
                serial_values.add((double) System.currentTimeMillis() - fstTime);
                System.out.println("Done Serial " + filter);
                fstTime = System.currentTimeMillis();
                Set<Cluster> parallel_kmeans_clusters = ParallelKMeans.run(cities, 50, 100, 1);
                parallel_values.add((double) System.currentTimeMillis() - fstTime);
                System.out.println("Done Parallel " + filter);

            });
            Graph1.Labels labels = new Graph1.Labels("Time over Dataset Dimension", "Number of cities", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Serial", serial_values));
            series.add(new Graph1.Serie("Parallel", parallel_values));

            ExampleChart<XYChart> exampleChart = new Graph1(labels, populations, series);
            XYChart chart = exampleChart.getChart();
            try {
                BitmapEncoder.saveBitmapWithDPI(chart, "./charts/Chart_1", BitmapEncoder.BitmapFormat.JPG, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ANSWER 2
        {
            List<Double> centers = new ArrayList<>();
            List<Double> serial_values = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            IntStream stream = IntStream.of(10, 20, 50, 99, 100, 150, 200);

            stream.forEach(number_of_centers -> {
                System.out.println("Doing number of cluster " + number_of_centers);
                centers.add((double) number_of_centers);

                long fstTime = System.currentTimeMillis();
                Set<Cluster> serial_kmeans_clusters = SerialKMeans.run(cities_original, number_of_centers, 100);
                serial_values.add((double) System.currentTimeMillis() - fstTime);
                fstTime = System.currentTimeMillis();
                Set<Cluster> parallel_kmeans_clusters = ParallelKMeans.run(cities_original, number_of_centers, 100, 1);
                parallel_values.add((double) System.currentTimeMillis() - fstTime);
            });

            Graph1.Labels labels = new Graph1.Labels("Time over Number of Clusters", "Number of clusters", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Serial", serial_values));
            series.add(new Graph1.Serie("Parallel", parallel_values));

            ExampleChart<XYChart> exampleChart = new Graph1(labels, centers, series);
            XYChart chart = exampleChart.getChart();
            try {
                BitmapEncoder.saveBitmapWithDPI(chart, "./charts/Chart_2", BitmapEncoder.BitmapFormat.JPG, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ANSWER 3
        /*
        {
            List<Double> interactions = new ArrayList<>();
            List<Double> serial_values = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            IntStream _20_100 = IntStream.iterate(10, i -> i <= 100, i -> i + 10);
            IntStream _100_1000 = IntStream.iterate(200, i -> i <= 1000, i -> i + 100);

            IntStream.concat(_20_100, _100_1000).forEach(interaction -> {
                System.out.println("Doing interaction " + interaction);
                interactions.add((double) interaction);

                long fstTime = System.currentTimeMillis();
                Set<Cluster> serial_kmeans_clusters = SerialKMeans.run(cities_original, 50, interaction);
                serial_values.add((double) System.currentTimeMillis() - fstTime);
                fstTime = System.currentTimeMillis();
                Set<Cluster> parallel_kmeans_clusters = ParallelKMeans.run(cities_original, 50, interaction, 1);
                parallel_values.add((double) System.currentTimeMillis() - fstTime);
            });

            Graph1.Labels labels = new Graph1.Labels("Time over Number of Interaction", "Number of interactions", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Serial", serial_values));
            series.add(new Graph1.Serie("Parallel", parallel_values));

            ExampleChart<XYChart> exampleChart = new Graph1(labels, interactions, series);
            XYChart chart = exampleChart.getChart();
            try {
                BitmapEncoder.saveBitmapWithDPI(chart, "./charts/Chart_3", BitmapEncoder.BitmapFormat.JPG, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         */

        // ANSWER 4
        {
            List<Double> cutoffs = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            IntStream _0_10 = IntStream.range(1, 10);
            IntStream _20_100 = IntStream.iterate(20, i -> i <= 100, i -> i + 10);
            IntStream _100_1000 = IntStream.iterate(200, i -> i <= 1000, i -> i + 100);
            IntStream _1000_ = IntStream.iterate(2_000, i -> i <= 38_000, i -> i + 1000);
            IntStream end = IntStream.of(cities_original.size());

            Stream.of(_0_10, _20_100, _100_1000, _1000_, end).reduce(IntStream::concat).get().forEach(cutoff -> {
                System.out.print("Doing cutoff " + cutoff + "\t");
                cutoffs.add((double) cutoff);

                long fstTime = System.currentTimeMillis();
                Set<Cluster> parallel_kmeans_clusters = ParallelKMeans.run(cities_original, 50, 100, cutoff);
                double value = (double) System.currentTimeMillis() - fstTime;
                parallel_values.add(value);
                System.out.println(value);
                System.gc();
                System.runFinalization();
            });

            Graph1.Labels labels = new Graph1.Labels("Time over Cutoff value", "Cutoff value", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Parallel", parallel_values));

            ExampleChart<XYChart> exampleChart = new Graph1(labels, cutoffs, series, true);
            XYChart chart = exampleChart.getChart();
            try {
                BitmapEncoder.saveBitmapWithDPI(chart, "./charts/Chart_4", BitmapEncoder.BitmapFormat.JPG, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
