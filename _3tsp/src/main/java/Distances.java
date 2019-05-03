import org.apache.commons.math3.ml.distance.EuclideanDistance;

public class Distances {
    static final EuclideanDistance euclideanDistance = new EuclideanDistance();
    static final int R = 6371;

    public static int euclidean_distance(double[] first, double[] second) {
        return (int) euclideanDistance.compute(first, second);
    }

    /**
     * TSPLIB geo distance
     */
    public static int calculateGeoDistance(double[] first, double[] second) {
        double lat1Rad = coordinates2Radians(first[0]);
        double lat2Rad = coordinates2Radians(second[0]);
        double lon1Rad = coordinates2Radians(first[1]);
        double lon2Rad = coordinates2Radians(second[1]);

        double R = 6378.388;
    
        double q1 = Math.cos(lon1Rad - lon2Rad);
        double q2 = Math.cos(lat1Rad - lat2Rad);
        double q3 = Math.cos(lat1Rad + lat2Rad);
    
        double distance = (R * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);

        return (int) distance;
    }
    
    private static double coordinates2Radians(double coordinate) {
        int deg = (int) coordinate;
        double min = coordinate - deg;
        double rad = (Math.PI * (deg + 5.0 * min / 3.0) / 180.0);
        return rad;
    }

    public static int distance(double[] first, double[] second, String type) {
        if (type.equals("EUC_2D"))
            return euclidean_distance(first, second);
        else if (type.equals("GEO")) 
            return calculateGeoDistance(first, second);
        else
            throw new IllegalArgumentException("Type distance non defined");
    }
}
