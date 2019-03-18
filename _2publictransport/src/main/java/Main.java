import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Graph g = generate_graph();

        System.out.println(g.calculateShortestPathFromSource(500000079 ,300000044, 1300));

        System.out.println(g.calculateShortestPathFromSource(200415016   ,200405005, 930));

        System.out.println(g.calculateShortestPathFromSource(300000032  ,400000122, 530));

        System.out.println(g.calculateShortestPathFromSource(210602003  ,300000030, 630));

        System.out.println(g.calculateShortestPathFromSource(200417051  ,140701016, 1200));

        System.out.println(g.calculateShortestPathFromSource(200417051  ,140701016, 2355));

        // per A* o Dikstra visto che ci sono Archi paralleli è necessario
        // filtrarli
        // memorizzare con Mappa il predecessore, ma serve salvare linea, tempi
    }

    static Graph generate_graph() throws IOException {
        Graph g = new Graph();
        Files.walk(Paths.get("public_transport_dataset"))
                .filter(x -> !x.endsWith("bahnhof") && !x.endsWith("bfkoord"))
                .forEach(x -> {
                    String content = "";
                    try {
                        content = new String(Files.readAllBytes(x));
                    } catch (IOException e) {
                    }

                    for (String line : content.split("\\*Z")) {
                        if(line.length()<13) continue;  // don't consider the first split
                        String name = line.substring(1, 13);
                        Integer source = null;
                        Integer dest;
                        Integer dept_time = null;

                        String[] lines = line.split("\n");

                        for (String l : lines) {
                            if (l.startsWith("*")) continue; // don't considere header file
                            if (l.length()<74) continue;     // don't consider name's row
                            dest = Integer.valueOf(l.substring(0, 9));
                            if (source != null) {
                                int arr_time = Integer.valueOf(l.substring(32, 37));
                                dest = Integer.valueOf(l.substring(0, 9));
                                g.add_connection(source, dest, name, dept_time, arr_time);
                            }
                            source = dest;
                            try {
                                dept_time = Integer.valueOf(l.substring(39, 44));
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                });
        return g;
    }
}
