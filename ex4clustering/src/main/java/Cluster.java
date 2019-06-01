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
        this.centroid = new Centroid(centroid.getX(), centroid.getY());
    }

    private Cluster(HashSet<County> cities, Centroid centroid) {
        this.cities = cities;
        this.centroid = centroid;
    }

    public String toString() {
        return (int) centroid.getX() + ";" + (int) centroid.getY();
    }

    static Cluster union(Cluster first, Cluster second) {
        HashSet<County> cities = new HashSet<>();
        cities.addAll(first.cities);
        cities.addAll(second.cities);

        return new Cluster(
                cities,
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
        int size = cities.size();

        double new_x = cities.stream().mapToDouble(County::getX).sum();
        double new_y = cities.stream().mapToDouble(County::getY).sum();

        return new Centroid(new_x/size, new_y/size);
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

    double getError() {
        return cities.stream().mapToDouble(county -> county.getPopulation() * Math.pow(centroid.distance(county), 2)).sum();
    }
}
