public class City {
    private final int id;
    private final String name;
    private final double longitude;
    private final double latitude;
    private final int population;

    public City(int id, String name, double latitude, double longitude, int population) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.population = population;
    }

    public String toString() {
        return "Lat: " + latitude + "\t" + "Lon: " + longitude + "\t" + "Pop: " + population;
    }

    public double getLat() {
        return latitude;
    }

    public double getLon() {
        return longitude;
    }

    public int getPopulation() {
        return population;
    }
}
