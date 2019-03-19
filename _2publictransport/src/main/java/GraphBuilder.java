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

                        for (String line : content.split("\\*Z")) {
                            if (line.length() < 13) continue;  // don't consider the first split
                            String name = line.substring(1, 13);
                            Integer source = null;
                            Integer dest;
                            Integer dept_time = null;

                            String[] lines = line.split("\n");

                            for (String l : lines) {
                                if (l.startsWith("*")) continue; // don't consider header file
                                if (l.length() < 74) continue;     // don't consider name's row
                                dest = Integer.valueOf(l.substring(0, 9));
                                if (source != null) {
                                    int arr_time = Integer.valueOf(l.substring(32, 37)) % 2400;
                                    dest = Integer.valueOf(l.substring(0, 9));
                                    g.add_connection(source, dest, name, dept_time % 2400, arr_time % 2400);
                                }
                                source = dest;
                                try {
                                    dept_time = Integer.valueOf(l.substring(39, 44)) % 2400;
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                    });
        } catch (IOException e) {
            throw new InstantiationError("Can' t find source folder " + "\n" + e.getLocalizedMessage());
        }
        return g;
    }
}
