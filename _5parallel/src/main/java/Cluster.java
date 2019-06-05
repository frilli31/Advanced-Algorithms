import java.util.ArrayList;
import java.util.List;

public class Cluster {
    List<City> cities;
    Centroid centroid;


    public Cluster(City city) {
        cities = new ArrayList<City>();
        cities.add(city);
        centroid = new Centroid(city);
    }

    public Cluster(List<City> cities) {
        this.cities = cities;
        this.centroid = getCentroid();
    }

    public Cluster(Centroid centroid, int estimated_size) {
        cities = new ArrayList<>(estimated_size);
        this.centroid = new Centroid(centroid.getLatitude(), centroid.getLongitude());
    }

    public String toString() {
        return (int) centroid.getLatitude() + ";" + (int) centroid.getLongitude();
    }

    void insert(City county) {
        cities.add(county);
    }

    Centroid getCentroid() {
        int size = cities.size();

        //double new_latitude = cities.stream().mapToDouble(City::getLat).sum();
        //double new_longitude = cities.stream().mapToDouble(City::getLon).sum();
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

    double distance(City county) {
        return centroid.distance(county);
    }

    double getError() {
        return cities.stream().mapToDouble(county -> county.getPopulation() * Math.pow(centroid.distance(county), 2)).sum();
    }
}
