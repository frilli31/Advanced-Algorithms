import java.util.HashSet;

public class Cluster {
    HashSet<County> counties;
    Centroid centroid;


    public Cluster(County county) {
        counties = new HashSet<County>();
        counties.add(county);
        centroid = new Centroid(county);
    }

    public Cluster(Centroid centroid) {
        counties = new HashSet<>();
        this.centroid = new Centroid(centroid.getX(), centroid.getY());
    }

    private Cluster(HashSet<County> counties, Centroid centroid) {
        this.counties = counties;
        this.centroid = centroid;
    }

    public String toString() {
        return (int) centroid.getX() + ";" + (int) centroid.getY();
    }

    static Cluster union(Cluster first, Cluster second) {
        HashSet<County> counties = new HashSet<>();
        counties.addAll(first.counties);
        counties.addAll(second.counties);

        return new Cluster(
                counties,
                Centroid.union(first.centroid, second.centroid)
        );
    }

    void insert(County county) {
        counties.add(county);
    }

    void insertAndUpdateCentroid(County county) {
        counties.add(county);
        centroid.update(county);
    }

    Centroid getCentroid() {
        int size = counties.size();

        double new_x = counties.stream().mapToDouble(County::getX).sum();
        double new_y = counties.stream().mapToDouble(County::getY).sum();

        return new Centroid(new_x/size, new_y/size);
    }

    void updateCentroid() {
        centroid = getCentroid();
    }

    void cleanCounties() {
        counties = new HashSet<County>();
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
        return counties.stream().mapToDouble(county -> county.getPopulation() * Math.pow(centroid.distance(county), 2)).sum();
    }
}
