import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    static public Set<City> get(String fileName) {
        String fileContent = readFile("input/" + fileName + ".csv");
        return parseCities(fileContent);
    }

    static String readFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            throw new InstantiationError("Can't find file of graph " + fileName);
        }
    }

    static Set<City> parseCities(String fileContent) {
        String replaced = fileContent.replace(" ", "");
        Pattern patternOfDimension = Pattern.compile("\n(\\d+),([^,]+),([^,]+),([^,]+),(\\S*)");
        Matcher matcherOfDimension = patternOfDimension.matcher(replaced);
        Set<City> cities = new HashSet<>();

        matcherOfDimension.results().forEach(x -> {
            cities.add(new City(
                    Integer.parseInt(x.group(1)),
                    x.group(2),
                    Double.parseDouble(x.group(4)),
                    Double.parseDouble(x.group(5)),
                    Integer.parseInt(x.group(3))
            ));
        });
        return cities;
    }
}

