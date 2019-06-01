import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.demo.charts.ExampleChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Basic Bubble Chart
 * <p>
 * Demonstrates the following:
 * <ul>
 * <li>Bubble Chart
 */
class BubbleChart01 implements ExampleChart<BubbleChart> {

    Set<Cluster> clusters;

    BubbleChart01(Set<Cluster> clusters) {
        this.clusters = clusters;
    }


    @Override
    public BubbleChart getChart() {

        // Create Chart
        BubbleChart chart = new BubbleChartBuilder().width(1200).height(900).title("BubbleChart01").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setAxisTicksVisible(false);

        List<Cluster> clusters2 = new ArrayList<>(clusters);

        for (int i = 0; i < clusters.size(); i++) {
            ArrayList<Double> xData = new ArrayList<>();
            ArrayList<Double> yData = new ArrayList<>();
            ArrayList<Double> population = new ArrayList<>();

            Cluster cluster = clusters2.get(i);

            for (County county : cluster.counties) {
                xData.add(county.getX());
                yData.add(600-county.getY());
                population.add(Math.sqrt(county.getPopulation() / 5000));
            }

            chart.addSeries("" + i, xData.stream().mapToDouble(z -> z).toArray(), yData.stream().mapToDouble(z -> z).toArray(), population.stream().mapToDouble(z -> z).toArray());
        }

        return chart;
    }

}