public class Centroid {
    private double x;
    private double y;

    private double sum_of_x;
    private double sum_of_y;

    private int item_count;

    Centroid(double x, double y) {
        this.x = x;
        this.y = y;
        item_count = 0;
    }

    Centroid(double sum_of_x, double sum_of_y, int number_of_elements) {
        this.sum_of_x = sum_of_x;
        this.sum_of_y = sum_of_y;
        this.item_count = number_of_elements;

        x = sum_of_x / item_count;
        y = sum_of_y / item_count;
    }

    Centroid(County county) {
        x = sum_of_x = county.getX();
        y = sum_of_y = county.getY();
        item_count = 1;
    }

    public String toString() {
        return x + " " + y + " " + item_count;
    }

    static Centroid union(Centroid first, Centroid second) {
        return new Centroid(
                first.sum_of_x + second.sum_of_x,
                first.sum_of_y + second.sum_of_y,
                first.item_count + second.item_count
        );
    }

    void update(County county) {
        sum_of_x += county.getX();
        sum_of_y += county.getY();
        item_count += 1;

        x = sum_of_x / item_count;
        y = sum_of_y / item_count;
    }

    double distance(Centroid other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }

    double distance(County other) {
        return Math.sqrt((x - other.getX()) * (x - other.getX()) + (y - other.getY()) * (y - other.getY()));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
