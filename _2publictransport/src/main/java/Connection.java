public class Connection {
    String name;
    int key_arrival_station;
    int departure_time;
    int arrival_time;

    public Connection(String n, int departure_t, int arrival_t) {
        name = n;
        departure_time = departure_t;
        arrival_time = arrival_t;
    }

    public int arrival_time() {
        return arrival_time;
    }
}
