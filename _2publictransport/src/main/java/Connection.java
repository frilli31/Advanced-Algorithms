import java.time.LocalTime;

public class Connection {
    String name;
    LocalTime departureTime;
    LocalTime arrivalTime;

    public Connection(String n, String departure_t, String arrival_t) {
        name = n;
        departureTime = LocalTime.of(Integer.valueOf(departure_t.substring(1, 3)) % 24, Integer.valueOf(departure_t.substring(3)));
        arrivalTime = LocalTime.of(Integer.valueOf(arrival_t.substring(1, 3)) % 24, Integer.valueOf(arrival_t.substring(3)));
    }
}
