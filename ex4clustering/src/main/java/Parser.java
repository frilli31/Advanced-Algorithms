import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    static public Set<County> get(String fileName) {
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

    static Set<County> parseCities(String fileContent) {
        String replaced = fileContent.replace(" ", "");
        Pattern patternOfDimension = Pattern.compile("(\\d+),([^,]+),([^,]+),(\\d+),(\\S*)");
        Matcher matcherOfDimension = patternOfDimension.matcher(replaced);
        Set<County> cities = new HashSet<>();

        matcherOfDimension.results().forEach(x -> {
                cities.add(new County(
                                Integer.parseInt(x.group(1)),
                                Double.parseDouble(x.group(2)),
                                Double.parseDouble(x.group(3)),
                                Integer.parseInt(x.group(4)),
                                Double.parseDouble(x.group(5))
                        ));
        });
        return cities;
    }
}

