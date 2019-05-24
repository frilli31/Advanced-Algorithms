import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.demo.charts.ExampleChart;

import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        //Set<County> cities_212 = Parser.get("unifiedCancerData_212");
        Set<County> cities_562 = Parser.get("unifiedCancerData_562");
        //Set<County> cities_1041 = Parser.get("unifiedCancerData_1041");
        Set<County> cities_3108 = Parser.get("unifiedCancerData_3108");

        ExampleChart<BubbleChart> exampleChart = new BubbleChart01(HierarchicalClustering.run(cities_3108, 15));
        //ExampleChart<BubbleChart> exampleChart2 = new BubbleChart01(ClusteringKMeans.run(cities_3108, 15, 5));
        BubbleChart chart = exampleChart.getChart();

        try {

            BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.JPG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SwingWrapper<BubbleChart>(chart).displayChart();
    }
}
