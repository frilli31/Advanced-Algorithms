import org.apache.commons.math3.ml.distance.EuclideanDistance;

public class Distances {
    static final EuclideanDistance euclideanDistance = new EuclideanDistance();
    static final int R = 6371;

    public static int euclidean_distance(double[] first, double[] second) {
        return (int) euclideanDistance.compute(first, second);
    }

    public static int geographic_distance(double[] first, double[] second) {
        double latDistance = Math.toRadians(second[1] - first[1]);
        double lonDistance = Math.toRadians(second[0] - first[0]);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(first[1])) * Math.cos(Math.toRadians(second[1]))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Km
        return (int) distance;
    }

    public static int distance(double[] first, double[] second, String type) {
        if (type.equals("EUC_2D"))
            return euclidean_distance(first, second);
        else if (type.equals("GEO"))
            return geographic_distance(first, second);
        else
            throw new IllegalArgumentException("Type distance non defined");
    }
}
