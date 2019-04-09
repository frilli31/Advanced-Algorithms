import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphBuilderTest {
    String content = "NAME: burma14\n" +
            "TYPE: TSP\n" +
            "COMMENT: 14-Staedte in Burma (Zaw Win)\n" +
            "DIMENSION: 14\n" +
            "EDGE_WEIGHT_TYPE: GEO\n" +
            "EDGE_WEIGHT_FORMAT: FUNCTION \n" +
            "DISPLAY_DATA_TYPE: COORD_DISPLAY\n" +
            "NODE_COORD_SECTION\n" +
            "   1  16.47       96.10\n" +
            "   2  16.47       94.44\n" +
            "   3  20.09       92.54\n" +
            "   4  22.39       93.37\n" +
            "   5  25.23       97.24\n" +
            "   6  22.00       96.05\n" +
            "   7  20.47       97.02\n" +
            "   8  17.20       96.29\n" +
            "   9  16.30       97.38\n" +
            "  10  14.05       98.12\n" +
            "  11  16.53       97.38\n" +
            "  12  21.52       95.59\n" +
            "  13  19.41       97.13\n" +
            "  14  20.09       94.55\n" +
            "EOF\n" +
            "\n" +
            "\n" +
            "\n";


    @Test
    void readFileTest() {
        String readed_content = GraphBuilder.readFile("tsp-dataset/burma14.tsp");

        assertEquals(content, readed_content);
    }

    @Test
    void parseDimensionTest() {
        assertEquals(14, GraphBuilder.parseDimension(content));
    }

    @Test
    void parseDistanceType() {
        assertEquals("GEO", GraphBuilder.parseDistanceType(content));
    }

    @Test
    void parseCoordinatesTest() {
        assertEquals(14, GraphBuilder.parseCoordinates(content).size());
    }





}
