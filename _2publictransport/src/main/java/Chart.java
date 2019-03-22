import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities;

import java.awt.Color;
import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Random;

public class Chart {
    static XYSeriesCollection dataset;
    static Map<Integer, Station> stations;

    static { populate_stations(); }

    public static void populate_stations() {
        stations = ParseStation.parse();

        XYSeries stations_series = new XYSeries("Stations");
        stations.forEach( (code, station) -> stations_series.add(station.x, station.y));
        dataset = new XYSeriesCollection();
        dataset.addSeries(stations_series);
    }

    public Chart(List<Integer> route) {
        XYSeries path = new XYSeries(route.get(0)+" - "+route.get(route.size()-1));
        if(route.get(0) == 300000032)
            System.out.println("problema");

        route.stream().map(stations::get).forEachOrdered(item->{

                    path.add(item.x, item.y);
        });
        dataset.addSeries(path);
        export_chart(path.getKey().toString());
        dataset.removeSeries(path);
    }

    public void export_chart(String name) {
        JFreeChart xy_line_chart = ChartFactory.createXYLineChart(
                "Luxembourg transport map",
                "",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot xyPlot = (XYPlot) xy_line_chart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        xyPlot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, false);
        renderer.setSeriesPaint(0, Color.LIGHT_GRAY);

        Random rand = new Random();

        for(int i = 0; i < dataset.getSeriesCount() - 1; i++) {
            renderer.setSeriesStroke ( i + 1, new BasicStroke (3.0f));

            Color c = Color.BLUE;

            switch(i + 1) {
                case 1:
                    c = Color.BLUE;
                    break;
                case 2:
                    c = Color.RED;
                    break;
                case 3:
                    c = Color.GREEN;
                    break;

                case 4:
                    c = Color.MAGENTA;
                    break;

                case 5:
                    c = Color.ORANGE;
                    break;
                case 6:
                    c = Color.CYAN;
                    break;
            }

            renderer.setSeriesPaint(i + 1, c);
            renderer.setSeriesShapesVisible(i + 1, true);
        }

        xyPlot.setRenderer(renderer);

        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setVisible(false);

        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        range.setRange(47.2, 51);
        range.setTickUnit(new NumberTickUnit(0.000001));
        range.setVisible(false);

        int width = 3000;
        int height = 1500;
        File chart = new File(System.nanoTime() + " - "  + name + "_chart.jpeg");
        try {
            ChartUtilities.saveChartAsJPEG(chart, xy_line_chart, width, height);
        } catch (IOException e) {}
    }
}