import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphBuilder {

    static Graph build(String folder) {
        Graph g = new Graph();
        try {
            Files.walk(Paths.get(folder))
                    .filter(x -> !x.endsWith("bahnhof") && !x.endsWith("bfkoord"))
                    .forEach(x -> {
                        String content = "";
                        try {
                            content = new String(Files.readAllBytes(x));
                        } catch (IOException e) {
                        }

                        for (String route : content.split("(?=\\*Z)")) {
                            if (route.length() < 13) continue;  // don't consider the first row
                            String name = route.substring(3, 15);
                            Integer source = null;
                            Integer dest;
                            String dept_time = null;

                            String[] lines = route.split("\n");

                            for (String l : lines) {
                                if (l.startsWith("*")) continue; // don't consider header line
                                dest = Integer.valueOf(l.substring(0, 9));
                                if (source != null) {
                                    String arr_time = l.substring(32, 37);
                                    g.add_connection(source, dest, name, dept_time, arr_time);
                                }
                                source = dest;
                                dept_time = l.substring(39, 44);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new InstantiationError("Can' t find source folder " + "\n" + e.getLocalizedMessage());
        }
        return g;
    }
}
