import org.apache.commons.math3.util.FastMath;

public class Centroid {
    /**
     * TSPLIB geo distance
     */
    private static final double R = 6378.388;
    private static final double PI = 3.141592;


    double latitude;
    double longitude;

    Centroid(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Centroid(City city) {
        latitude = city.getLat();
        longitude = city.getLon();
    }

    public String toString() {
        return latitude + " " + longitude + " ";
    }


    private static double coordinates2Radians(double coordinate) {
        int deg = (int) coordinate;
        return (PI * (deg + 5.0 * (coordinate - deg) / 3.0) / 180.0);
    }

    public int distance(City city) {
        //return (int) (FastMath.abs(latitude-city.getLat())+ FastMath.abs(longitude-city.getLon()));
        double lat1Rad = coordinates2Radians(latitude);
        double lat2Rad = coordinates2Radians(city.getLat());
        double lon1Rad = coordinates2Radians(longitude);
        double lon2Rad = coordinates2Radians(city.getLon());

        double q1 = FastMath.cos(lon1Rad - lon2Rad);
        double q2 = FastMath.cos(lat1Rad - lat2Rad);
        double q3 = FastMath.cos(lat1Rad + lat2Rad);

        return (int) (R * FastMath.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
