import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;

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

    public Pair<String, List<Integer>> AStarSSSP(int source, int destination, LocalTime startTime) {
        Map<Integer, Station> stations = ParseStation.parse();
        Station sourceStation = stations.get(source);
        Station destStation = stations.get(destination);
        Set<Integer> visitedNodes = new HashSet<>();
        Map<Integer, Long> minDistance = new HashMap<>();
        Map<Integer, Double> geoDistance = new HashMap<>();
        Map<Integer, LocalTime> arrivalTime = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Map<Integer, Connection> prevConnection = new HashMap<>();
        AStarHeap unvisitedNodes = new AStarHeap(minDistance, geoDistance);

        unvisitedNodes.add(source);
        minDistance.put(source, Long.valueOf(0));
        geoDistance.put(source, calculateGeoDistance(sourceStation, destStation));
        arrivalTime.put(source, startTime);

        while (unvisitedNodes.size() != 0) {
            int parent = unvisitedNodes.extractMin();

            if (parent == destination) break;

            adjs.get(parent).entrySet().stream().filter(e -> !visitedNodes.contains(e.getKey())).forEach(e -> {
                int dest = e.getKey();
                LocalTime parentArrivalTime = arrivalTime.get(parent);
                Connection conn = e.getValue().stream().min((Connection a, Connection b) -> {
                    Long minutesA = calculateMinDistance(a, parentArrivalTime);
                    Long minutesB = calculateMinDistance(b, parentArrivalTime);

                    // If the distance is equal remain in the same bus
                    Boolean canRemainInBus = minutesA == minutesB && parent != source && prevConnection.get(parent).name.equals(a.name);
        
                    return minutesA < minutesB || canRemainInBus ? -1 : 1;
                }).orElseThrow();
                Long connDistance = minDistance.get(parent) + calculateMinDistance(conn, parentArrivalTime);
                
                if (!minDistance.containsKey(dest) || connDistance < minDistance.get(dest)) {
                    Double connGeoDistance = calculateGeoDistance(stations.get(dest), destStation);

                    minDistance.put(dest, connDistance);
                    geoDistance.put(dest, connGeoDistance);
                    arrivalTime.put(dest, conn.arrivalTime);
                    prev.put(dest, parent);
                    prevConnection.put(dest, conn);
                    unvisitedNodes.decreaseKey(dest, connDistance, connGeoDistance);
                }
                
                unvisitedNodes.add(dest);
            });
            visitedNodes.add(parent);
        }

        return formatOutput(source, destination, startTime, arrivalTime, prev, prevConnection);
    }

    public Pair<String, List<Integer>> djkstraHeapSSSP(int source, int destination, LocalTime startTime) {
        Set<Integer> visitedNodes = new HashSet<>();
        Map<Integer, Long> minDistance = new HashMap<>();
        Map<Integer, LocalTime> arrivalTime = new HashMap<>();
        Map<Integer, Integer> prevStation = new HashMap<>();
        Map<Integer, Connection> prevConnection = new HashMap<>();
        Heap unvisitedNotes = new Heap(minDistance);

        unvisitedNotes.add(source);
        minDistance.put(source, Long.valueOf(0));
        arrivalTime.put(source, startTime);

        while (unvisitedNotes.size() != 0) {
            int parent = unvisitedNotes.extractMin();

            if (parent == destination) break;

            adjs.get(parent).entrySet().stream().filter(e -> !visitedNodes.contains(e.getKey())).forEach(e -> {
                int dest = e.getKey();
                LocalTime parentArrivalTime = arrivalTime.get(parent);
                Connection conn = e.getValue().stream().min((Connection a, Connection b) -> {
                    Long minutesA = calculateMinDistance(a, parentArrivalTime);
                    Long minutesB = calculateMinDistance(b, parentArrivalTime);

                    // If the distance is equal remain in the same bus
                    Boolean canRemainInBus = minutesA == minutesB && parent != source && prevConnection.get(parent).name.equals(a.name);
        
                    return minutesA < minutesB || canRemainInBus ? -1 : 1;
                }).orElseThrow();
                Long connDistance = minDistance.get(parent) + calculateMinDistance(conn, parentArrivalTime);

                if (!minDistance.containsKey(dest) || connDistance < minDistance.get(dest)) {
                    minDistance.put(dest, connDistance);
                    arrivalTime.put(dest, conn.arrivalTime);
                    prevStation.put(dest, parent);
                    prevConnection.put(dest, conn);
                    unvisitedNotes.decreaseKey(dest, connDistance);
                }
                
                unvisitedNotes.add(dest);
            });
            visitedNodes.add(parent);
        }

        return formatOutput(source, destination, startTime, arrivalTime, prevStation, prevConnection);
    }

    static double calculateGeoDistance(Station source, Station destination) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(destination.y - source.y);
        double lonDistance = Math.toRadians(destination.x - source.x);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(source.y)) * Math.cos(Math.toRadians(destination.y))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Km

        return distance / 120 * 60;
    }

    static Long calculateMinDistance(Connection conn, LocalTime startTime) {
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

    Pair<String, List<Integer>> formatOutput(int source, int destination, LocalTime startTime,
            Map<Integer, LocalTime> arrivalTime, Map<Integer, Integer> prevStation,
            Map<Integer, Connection> prevConnection) {
        if (arrivalTime.containsKey(destination)) {
            StringBuilder message = new StringBuilder();
            LinkedList<Integer> path = new LinkedList<>();

            int station = destination;

            while (prevStation.containsKey(station)) {
                int arrival_station = station;
                path.push(station);
                Connection last = prevConnection.get(arrival_station);
                Connection first = prevConnection.get(arrival_station);
                Connection iter = prevConnection.get(arrival_station);
                while (last.name.equals(iter.name)) {
                    first = prevConnection.get(station);
                    station = prevStation.get(station);
                    iter = prevConnection.get(station);
                    path.push(station);
                    if (!prevStation.containsKey(station))
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
