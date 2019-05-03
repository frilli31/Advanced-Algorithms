import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class GraphBuilder {

    static public Graph get(String fileName) {
        String fileContent = readFile("tsp-dataset/" + fileName + ".tsp");
        String distanceType = parseDistanceType(fileContent);
        List<double[]> coordinatesOfPoints = parseCoordinates(fileContent);
        int[][] populated_matrix = calculateMatrixOfDistances(coordinatesOfPoints, distanceType);
        return new Graph(fileName, populated_matrix);
    }

    static String readFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            throw new InstantiationError("Can't find file of graph " + fileName);
        }
    }

    static int parseDimension(String fileContent) {
        Pattern patternOfDimension = Pattern.compile("DIMENSION\\W+(\\d+)\\s");
        Matcher matcherOfDimension = patternOfDimension.matcher(fileContent);
        if (matcherOfDimension.find())
            return Integer.valueOf(matcherOfDimension.group(1));
        throw new IllegalStateException("No dimension found in file");
    }

    static String parseDistanceType(String fileContent) {
        Pattern patternOfDimension = Pattern.compile("EDGE_WEIGHT_TYPE\\W+(\\w+)\\s");
        Matcher matcherOfDimension = patternOfDimension.matcher(fileContent);
        if (matcherOfDimension.find())
            return matcherOfDimension.group(1);
        throw new IllegalStateException("No distance type found in file");
    }

    static List<double[]> parseCoordinates(String fileContent) {
        Pattern patternOfDimension = Pattern.compile(" *(\\d+) +(\\S+) +(\\S+)\\n");
        Matcher matcherOfDimension = patternOfDimension.matcher(fileContent);
        List<double[]> coordinates = new ArrayList<>(parseDimension(fileContent));

        matcherOfDimension.results().forEach(x -> {
            coordinates.add(new double[] { Double.parseDouble(x.group(2)), Double.parseDouble(x.group(3)) });
        });
        return coordinates;
    }

    static int[][] calculateMatrixOfDistances(List<double[]> coordinates, String distanceType) {
        final int size = coordinates.size();
        int[][] matrix = new int[size][size];

        IntStream.range(0, size).forEach(source -> {
            matrix[source][source] = 0;
            IntStream.range(source + 1, size)
                    .forEach(destination -> matrix[source][destination] = matrix[destination][source] = Distances
                            .distance(coordinates.get(source), coordinates.get(destination), distanceType));
        });
        return matrix;
    }
}
