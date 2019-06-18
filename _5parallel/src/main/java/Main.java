import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.demo.charts.ExampleChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        List<City> cities_original = new ArrayList<>(Parser.get("cities-and-towns-of-usa"));

        System.out.println(get_time(() ->ParallelKMeans.run(cities_original, 99, 100, 1)));
        System.out.println(get_time(() ->ParallelKMeans.run(cities_original, 50, 100, 100_000)));
        System.out.println(get_time(() ->SerialKMeans.run(cities_original, 50, 100)));

        /*

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
                serial_values.add(get_time(() -> SerialKMeans.run(cities, 50, 100)));
                parallel_values.add(get_time(() -> ParallelKMeans.run(cities, 50, 100, 1)));
            });
            Graph1.Labels labels = new Graph1.Labels("Time over Dataset Dimension", "Number of cities", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Serial", serial_values));
            series.add(new Graph1.Serie("Parallel", parallel_values));

            export_chart("Chart_1", labels, populations, series, false);
        }

        // ANSWER 2
        {
            List<Double> centers = new ArrayList<>();
            List<Double> serial_values = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            IntStream.iterate(10, i -> i<=100, i -> i+10).forEach(number_of_centers -> {
                System.out.println("Doing number of cluster " + number_of_centers);
                centers.add((double) number_of_centers);
                serial_values.add(get_time(() -> SerialKMeans.run(cities_original, number_of_centers, 100)));
                parallel_values.add(get_time(() -> ParallelKMeans.run(cities_original, number_of_centers, 100, 1)));
            });

            Graph1.Labels labels = new Graph1.Labels("Time over Number of Clusters", "Number of clusters", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Serial", serial_values));
            series.add(new Graph1.Serie("Parallel", parallel_values));

            export_chart("Chart_2", labels, centers, series, false);
        }

        // ANSWER 3
        {
            List<Double> interactions = new ArrayList<>();
            List<Double> serial_values = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            IntStream _20_100 = IntStream.iterate(10, i -> i <= 100, i -> i + 10);
            IntStream _100_1000 = IntStream.iterate(400, i -> i <= 1000, i -> i + 300);

            IntStream.concat(_20_100, _100_1000).forEach(interaction -> {
                System.out.println("Doing interaction " + interaction);

                interactions.add((double) interaction);
                serial_values.add(get_time(() -> SerialKMeans.run(cities_original, 50, interaction)));
                parallel_values.add(get_time(() -> ParallelKMeans.run(cities_original, 50, interaction, 1)));

            });

            Graph1.Labels labels = new Graph1.Labels("Time over Number of Interaction", "Number of interactions", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Serial", serial_values));
            series.add(new Graph1.Serie("Parallel", parallel_values));

            export_chart("Chart_3", labels, interactions, series, false);
        }

        // ANSWER 4
        {
            List<Double> cutoffs = new ArrayList<>();
            List<Double> parallel_values = new ArrayList<>();

            IntStream.iterate(1, i -> i <= cities_original.size(), i -> i * 2).forEach(cutoff -> {
                System.out.println("Doing cutoff " + cutoff);
                cutoffs.add((double) cutoff);
                parallel_values.add(get_time(() -> ParallelKMeans.run(cities_original, 50, 100, cutoff)));
            });

            Graph1.Labels labels = new Graph1.Labels("Time over Cutoff value", "Cutoff value", "Time in ms");
            List<Graph1.Serie> series = new ArrayList<>();
            series.add(new Graph1.Serie("Parallel", parallel_values));
            export_chart("Chart_4", labels, cutoffs, series, true);
        }*/
    }

    private static double get_time(Supplier<Set<Cluster>> f) {
        long start_time = System.currentTimeMillis();
        f.get();
        return System.currentTimeMillis() - start_time;
    }

    private static void export_chart(String name, Graph1.Labels labels, List<Double> x_serie, List<Graph1.Serie> series, boolean is_log) {
        ExampleChart<XYChart> exampleChart = new Graph1(labels, x_serie, series, is_log);
        XYChart chart = exampleChart.getChart();
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./charts/" + name, BitmapEncoder.BitmapFormat.JPG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
