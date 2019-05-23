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
        return "Cluster: " + cities.toString() + ", " + centroid;
    }

    /*static Cluster union(Cluster first, Cluster second) {
        HashSet<County> mine = new HashSet<>();
        mine.addAll(first.cities);
        mine.addAll(second.cities);

        return new Cluster(
                mine,
                Centroid.union(first.centroid, second.centroid)
        );
    }*/

    void insert(County county) {
        cities.add(county);
    }

    void insertAndUpdateCentroid(County county) {
        cities.add(county);
        centroid.update(county);
    }

    Centroid getCentroid() {
        return new Centroid(centroid.getX(), centroid.getY());
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
