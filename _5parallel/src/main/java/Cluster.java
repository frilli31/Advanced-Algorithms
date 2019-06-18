import java.util.ArrayList;
import java.util.List;

public class Cluster {
    List<City> cities;
    Centroid centroid;


    public Cluster(City city) {
        cities = new ArrayList<>();
        cities.add(city);
        centroid = new Centroid(city);
    }

    public Cluster(Centroid centroid) {
        cities = new ArrayList<>();
        this.centroid = centroid;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public Cluster(List<City> cities) {
        this.cities = cities;
        this.centroid = getCentroid();
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    public String toString() {
        return "Size: " + cities.size() + "\tCoord: " + (int) centroid.getLatitude() + "\t" + (int) centroid.getLongitude();
    }

    void insert(City city) {
        cities.add(city);
    }

    void updateCentroid() {
        centroid = getCentroid();
    }

    void cleanCounties(int estimated_size) {
        cities = new ArrayList<>(estimated_size);
    }

    Centroid getCentroid() {
        int size = cities.size();

        if (size == 0) {
            return centroid;
        }

        double new_latitude = 0;
        double new_longitude = 0;
        for (City city : cities) {
            new_latitude += city.getLat();
            new_longitude += city.getLon();
        }
        return new Centroid(new_latitude / size, new_longitude / size);
    }

    double getLatitude() {
        return centroid.getLatitude();
    }

    double getLongitude() {
        return centroid.getLongitude();
    }

    int distance(City city) {
        return centroid.distance(city);
    }
}
