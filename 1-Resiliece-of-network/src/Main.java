import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("as20000102.txt"), StandardCharsets.UTF_8);
        Graph g = new Graph(lines);
        Graph er = new ER(6474, 0.0003);
        Graph dpa = new DPA(6474, 2);

        double percentage = 20;
        List<Graph> list = Stream.of(g, er, dpa).collect(Collectors.toList());
        list.forEach(Graph::printInfo);
        list.forEach(x -> {
            x.bestNodeRemove(1);
            System.out.println(x.resilience());
            x.printInfo();
        });
    }
}
