import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class Chart {

    public Chart(Set<Cluster> route) {
        String name = route.get(0)+"_"+route.get(route.size()-1);
        ArrayList<XYSeries> series = new ArrayList<>();
        for(int i=0; i<route.size()-1; i++) {
            Station me = stations.get(route.get(i));
            Station next = stations.get(route.get(i+1));
            XYSeries s = new XYSeries(me.name+"-"+next.name);
            s.add(me.x, me.y);
            s.add(next.x, next.y);
            series.add(s);
        }
        series.forEach(dataset::addSeries);
        exportChart(name);
        series.forEach(dataset::removeSeries);
    }

    public void exportChart(String name) {
        JFreeChart xy_line_chart = ChartFactory.createXYLineChart(
                name,
                "",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        Shape shape  = new Ellipse2D.Double(0,0,2,2);

        XYPlot xyPlot = (XYPlot) xy_line_chart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        xyPlot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        renderer.setSeriesShape(0, shape);
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, false);
        renderer.setSeriesPaint(0, Color.LIGHT_GRAY);

        for(int i = 0; i < dataset.getSeriesCount() - 1; i++) {
            renderer.setSeriesStroke ( i + 1, new BasicStroke (3.0f));
            Color c = Color.BLUE;
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

        int width = 1500;
        int height = 1500;
        File chart = new File(name + ".jpeg");
        try {
            ChartUtilities.saveChartAsJPEG(chart, xy_line_chart, width, height);
        } catch (IOException e) {}
    }
}

