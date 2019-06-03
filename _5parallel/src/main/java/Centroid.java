public class Centroid {
    private double latitude;
    private double longitude;

    Centroid(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Centroid(City city) {
        latitude = city.getLat();
        longitude = city.getLon();
    }

    private static double coordinates2Radians(double coordinate) {
        int deg = (int) coordinate;
        double min = coordinate - deg;
        double rad = (Math.PI * (deg + 5.0 * min / 3.0) / 180.0);
        return rad;
    }

    public String toString() {
        return latitude + " " + longitude + " ";
    }

    /**
     * TSPLIB geo distance
     */
    public int distance(City city) {
        double lat1Rad = coordinates2Radians(latitude);
        double lat2Rad = coordinates2Radians(city.getLat());
        double lon1Rad = coordinates2Radians(longitude);
        double lon2Rad = coordinates2Radians(city.getLon());

        double R = 6378.388;

        double q1 = Math.cos(lon1Rad - lon2Rad);
        double q2 = Math.cos(lat1Rad - lat2Rad);
        double q3 = Math.cos(lat1Rad + lat2Rad);

        double distance = (R * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);

        return (int) distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
