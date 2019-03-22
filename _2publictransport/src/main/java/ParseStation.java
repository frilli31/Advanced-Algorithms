import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParseStation {

    static Map<Integer, Station> parse() {
        Map<Integer, Station> stations = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "public_transport_dataset/bfkoord"));
            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("*") && !line.startsWith("%")) {
                    int code = Integer.parseInt(line.substring(0, 9));
                    double x = Double.parseDouble(line.substring(12, 20));
                    double y = Double.parseDouble(line.substring(22, 31));
                    String name = line.split("% ")[1];

                    Station s = new Station(name, x, y);
                    stations.put(code, s);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  stations;
    }
}
