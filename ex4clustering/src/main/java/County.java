public class County {
    private final int id;
    private final double x;
    private final double y;
    private final int population;
    private final double cancer_danger;

    public County(int id, double x, double y, int population, double cancer_danger) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.population = population;
        this.cancer_danger = cancer_danger;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getPopulation() {
        return population;
    }
}
