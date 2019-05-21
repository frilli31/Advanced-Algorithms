import java.util.HashSet;

public class Cluster {
    HashSet<County> cities;
    Centroid centroid;


    public Cluster(County county) {
        cities = new HashSet<County>();
        cities.add(county);
        centroid = new Centroid(county);
    }

    public Cluster(Centroid centroid) {
        cities = new HashSet<>();
        this.centroid = centroid;
    }

    private Cluster(HashSet<County> cities, Centroid centroid) {
        this.cities = cities;
        this.centroid = centroid;
    }

    static Cluster union(Cluster first, Cluster second) {
        first.cities.addAll(second.cities);

        return new Cluster(
                first.cities,
                Centroid.union(first.centroid, second.centroid)
        );
    }

    void insert(County county) {
        cities.add(county);
    }

    void insertAndUpdateCentroid(County county) {
        cities.add(county);
        centroid.update(county);
    }

    Centroid getCentroid() {
        double x = centroid.getX() + cities.stream().mapToDouble(County::getX).sum();
        double y = centroid.getX() + cities.stream().mapToDouble(County::getY).sum();

        final int number_of_cities = cities.size() + 1;

        return new Centroid(x / number_of_cities, y / number_of_cities);
    }

    double getX() {
        return centroid.getX();
    }

    double getY() {
        return centroid.getY();
    }

    double distance(County county) {
        return centroid.distance(county);
    }

    double distance(Cluster cluster) {
        return centroid.distance(cluster.centroid);
    }
}
