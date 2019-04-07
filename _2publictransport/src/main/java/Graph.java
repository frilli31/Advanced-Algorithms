import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.Comparator;
import java.util.*;

import javafx.util.Pair;

public class Graph {
    private Map<Integer, Map<Integer, List<Connection>>> adjs;

    public Graph() {
        adjs = new HashMap<>();
    }

    public void add_station(int key) {
        adjs.putIfAbsent(key, new HashMap<>());
    }

    public void add_connection(int source, int dest, String name, String dep_time, String arr_time) {
        add_station(source);
        add_station(dest);
        Map<Integer, List<Connection>> adjs = this.adjs.get(source);

        adjs.putIfAbsent(dest, new ArrayList<>());
        adjs.get(dest).add(new Connection(name, dep_time, arr_time));
    }

    public Pair<String, List<Integer>> djkstraHeapSSSP(int source, int destination, LocalTime startTime) {
        Set<Integer> settledNodes = new HashSet<>();
        Map<Integer, Long> distance = new HashMap<>();
        Map<Integer, LocalTime> arrivalTime = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Map<Integer, Connection> prevConnection = new HashMap<>();
        Heap unsettledNodes = new Heap(distance);

        unsettledNodes.add(source);
        distance.put(source, Long.valueOf(0));
        arrivalTime.put(source, startTime);

        while (unsettledNodes.size() != 0) {
            int parent = unsettledNodes.extractMin();

            if (parent == destination) break;

            adjs.get(parent).entrySet().stream().filter(e -> !settledNodes.contains(e.getKey())).forEach(e -> {
                int dest = e.getKey();
                LocalTime parentArrivalTime = arrivalTime.get(parent);
                Connection conn = e.getValue().stream().min((Connection a, Connection b) -> {
                    Long minutesA = minutesDistance(a, parentArrivalTime);
                    Long minutesB = minutesDistance(b, parentArrivalTime);

                    // If the distance is equal remain in the same bus
                    Boolean canRemainInBus = minutesA == minutesB && parent != source && prevConnection.get(parent).name.equals(a.name);
        
                    return minutesA < minutesB || canRemainInBus ? -1 : 1;
                }).orElseThrow();
                Long connDistance = distance.get(parent) + minutesDistance(conn, parentArrivalTime);

                if (!distance.containsKey(dest) || connDistance < distance.get(dest)) {
                    distance.put(dest, connDistance);
                    arrivalTime.put(dest, conn.arrivalTime);
                    prev.put(dest, parent);
                    prevConnection.put(dest, conn);
                    unsettledNodes.decreaseKey(dest, connDistance);
                }
                
                unsettledNodes.add(dest);
            });
            settledNodes.add(parent);
        }

        return format_output(source, destination, startTime, arrivalTime, prev, prevConnection);
    }

    public Pair<String, List<Integer>> djkstraSetSSSP(int source, int destination, LocalTime startTime) {
        Set<Integer> settledNodes = new HashSet<>();
        Set<Integer> unsettledNodes = new HashSet<>();
        Map<Integer, Long> distance = new HashMap<>();
        Map<Integer, LocalTime> arrivalTime = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Map<Integer, Connection> prevConnection = new HashMap<>();

        unsettledNodes.add(source);
        distance.put(source, Long.valueOf(0));
        arrivalTime.put(source, startTime);

        while (unsettledNodes.size() != 0) {
            int parent = unsettledNodes.stream().min(Comparator.comparingLong(distance::get)).get();
            unsettledNodes.remove(parent);

            if (parent == destination) break;

            adjs.get(parent).entrySet().stream().filter(e -> !settledNodes.contains(e.getKey())).forEach(e -> {
                int dest = e.getKey();
                LocalTime parentArrivalTime = arrivalTime.get(parent);
                Connection conn = e.getValue().stream().min((Connection a, Connection b) -> {
                    Long minutesA = minutesDistance(a, parentArrivalTime);
                    Long minutesB = minutesDistance(b, parentArrivalTime);

                    // If the distance is equal remain in the same bus
                    Boolean canRemainInBus = minutesA == minutesB && parent != source && prevConnection.get(parent).name.equals(a.name);
        
                    return minutesA < minutesB || canRemainInBus ? -1 : 1;
                }).orElseThrow();
                Long connDistance = distance.get(parent) + minutesDistance(conn, parentArrivalTime);

                if (!distance.containsKey(dest) || connDistance < distance.get(dest)) {
                    distance.put(dest, connDistance);
                    arrivalTime.put(dest, conn.arrivalTime);
                    prev.put(dest, parent);
                    prevConnection.put(dest, conn);
                }
                
                unsettledNodes.add(dest);
            });
            settledNodes.add(parent);
        }

        return format_output(source, destination, startTime, arrivalTime, prev, prevConnection);
    }

    static Long minutesDistance(Connection conn, LocalTime startTime) {
        Boolean isInDay = afterOrEqual(conn.arrivalTime, conn.departureTime);

        if (afterOrEqual(conn.departureTime, startTime) && isInDay) {
            return MINUTES.between(startTime, conn.arrivalTime);
        } else if (afterOrEqual(conn.departureTime, startTime) && !isInDay) {
            return MINUTES.between(startTime, conn.arrivalTime) + 1440;
        } else { // conn.departure_time.isBefore(startTime)
            Long duration = isInDay
                ? MINUTES.between(conn.departureTime, conn.arrivalTime)
                : MINUTES.between(conn.departureTime, conn.arrivalTime) + 1440;

            return MINUTES.between(startTime, conn.departureTime) + 1440 + duration;
        }
    }

    static private Boolean afterOrEqual(LocalTime a, LocalTime b) {
        return a.compareTo(b) >= 0;
    }

    Pair<String, List<Integer>> format_output(int source, int destination, LocalTime startTime,
            Map<Integer, LocalTime> arrivalTime, Map<Integer, Integer> prev,
            Map<Integer, Connection> prevConnection) {
        if (arrivalTime.containsKey(destination)) {
            StringBuilder message = new StringBuilder();
            LinkedList<Integer> path = new LinkedList<>();

            int station = destination;

            while (prev.containsKey(station)) {
                int arrival_station = station;
                path.push(station);
                Connection last = prevConnection.get(arrival_station);
                Connection first = prevConnection.get(arrival_station);
                Connection iter = prevConnection.get(arrival_station);
                while (last.name.equals(iter.name)) {
                    first = prevConnection.get(station);
                    station = prev.get(station);
                    iter = prevConnection.get(station);
                    path.push(station);
                    if (!prev.containsKey(station))
                        break;
                }
                message.insert(0,
                        "\n" + first.departureTime + " -  "
                                + last.arrivalTime + " : corsa " + last.name + " da " + station
                                + " a " + arrival_station);
            }

            message.insert(0,
                    "Viaggio da " + source + " a " + destination + "\n" + "Orario di partenza: "
                            + startTime + "\n" + "Orario di arrivo: "
                            + arrivalTime.get(destination));
            return new Pair<>(message.toString(), path);
        }

        throw new IllegalArgumentException("Path does not exist");
    }
}
