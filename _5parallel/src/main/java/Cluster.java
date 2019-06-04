import java.util.HashSet;
import java.util.Set;

public class Cluster {
    Set<City> cities;
    Centroid centroid;


    public Cluster(City city) {
        cities = new HashSet<City>();
        cities.add(city);
        centroid = new Centroid(city);
    }

    public Cluster(Set<City> cities) {
        this.cities = cities;
        this.centroid = getCentroid();
    }

    public Cluster(Centroid centroid) {
        cities = new HashSet<>();
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

        //double new_x = cities.stream().mapToDouble(City::getLat).sum();
        //double new_y = cities.stream().mapToDouble(City::getLon).sum();
        double new_x = 0;
        double new_y = 0;
        for (City city : cities) {
            new_x += city.getLat();
            new_y += city.getLon();
        }
        return new Centroid(new_x / size, new_y / size);
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
