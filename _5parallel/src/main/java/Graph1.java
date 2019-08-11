import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.List;

public class Graph1 implements ExampleChart<XYChart> {
    List<Double> xData;
    List<Serie> series;
    Labels labels;
    boolean is_x_logarithmic;

    public Graph1(Labels labels, List<Double> xData, List<Serie> series, boolean is_x_logarithmic) {
        this.xData = xData;
        this.labels = labels;
        this.series = series;
        this.is_x_logarithmic = is_x_logarithmic;
    }

    public Graph1(Labels labels, List<Double> xData, List<Serie> series) {
        this(labels, xData, series, false);
    }

    @Override
    public XYChart getChart() {

        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).theme(Styler.ChartTheme.Matlab).title(labels.chartTitle).xAxisTitle(labels.xLabel).yAxisTitle(labels.yLabel).build();

        // Customize Chart
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setXAxisTickMarkSpacingHint(100);

        if (is_x_logarithmic)
            chart.getStyler().setXAxisLogarithmic(true);

        series.forEach(serie -> {
            XYSeries series = chart.addSeries(serie.name, xData, serie.data);
            series.setMarker(SeriesMarkers.NONE);
        });
        return chart;
    }

    static class Serie {
        String name;
        List<Double> data;

        Serie(String name, List<Double> data) {
            this.name = name;
            this.data = data;
        }
    }

    static class Labels {
        String xLabel;
        String yLabel;
        String chartTitle;

        public Labels(String title, String xLabel, String yLabel) {
            this.chartTitle = title;
            this.xLabel = xLabel;
            this.yLabel = yLabel;
        }
    }
}