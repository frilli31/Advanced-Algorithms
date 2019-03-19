import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.*;

public class Graph {
    private Map<Integer, Map<Integer, List<Connection>>> adjs; // Map<source, Map<destination, arcs>
    private Map<Integer, Station> stations;

    public Graph() {
        adjs = new HashMap<>();
        stations = new HashMap<>();
    }


    public void add_station(int key) {
        adjs.putIfAbsent(key, new HashMap<>());
    }

    public void add_connection(int source, int dest, String name, int dep_time, int arr_time) {
        add_station(source);
        add_station(dest);

        if (!adjs.get(source).containsKey(dest))
            adjs.get(source).put(dest, new ArrayList<>());
        adjs.get(source).get(dest).add(new Connection(name, dep_time, arr_time));
    }

    public String calculateShortestPathFromSource(int source, int destination, int start_time) {
        Set<Integer> settledNodes = new HashSet<>();
        Set<Integer> unsettledNodes = new HashSet<>();
        Map<Integer, Integer> distance = new HashMap<>();
        Map<Integer, Integer> precedent = new HashMap<>();
        Map<Integer, Connection> to_prec_connection = new HashMap<>();

        unsettledNodes.add(source);
        distance.put(source, start_time);

        while (unsettledNodes.size() != 0) {
            int parent = unsettledNodes.stream()
                    .min(Comparator.comparingInt(distance::get))
                    .get();
            unsettledNodes.remove(parent);

            if (parent == destination) {
                break;
            }

            // If the distance is equal remain in the same bus
            Comparator<Connection> comp = (x,y) -> {
                if(x.arrival_time<y.arrival_time || x.arrival_time<=y.arrival_time && parent!=source && to_prec_connection.get(parent).name.equals(x.name)) return -1;
                else return 1;
            };

            adjs.get(parent).entrySet().stream().filter(e -> !settledNodes.contains(e.getKey())).forEach(e -> {
                int dest = e.getKey();
                int time = distance.get(parent);
                Connection con = e.getValue().stream().filter(c -> c.departure_time >= time%2400 && c.arrival_time>=c.departure_time).min(comp).orElse(
                        e.getValue().stream().filter(c -> c.departure_time+2400 >= (time%2400)).min(comp).orElseThrow());
                int new_dist = con.arrival_time;
                if (con.arrival_time<time || con.arrival_time<con.departure_time) new_dist += 2400;

                if (!distance.containsKey(dest) || new_dist < distance.get(dest)) {
                    distance.put(dest, new_dist);
                    precedent.put(dest, parent);
                    to_prec_connection.put(dest, con);
                }
                unsettledNodes.add(dest);
            });
            settledNodes.add(parent);
        }

        if (distance.containsKey(destination)) {
            StringBuilder message = new StringBuilder();

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(':');
            DecimalFormat df_out = new DecimalFormat("0000;#", symbols);
            df_out.setGroupingUsed(true);
            df_out.setGroupingSize(2);

            int station = destination;
            while (precedent.containsKey(station)) {
                int arrival_station = station;
                Connection last = to_prec_connection.get(arrival_station);
                Connection first = to_prec_connection.get(arrival_station);
                Connection iter = to_prec_connection.get(arrival_station);
                while (last.name.equals(iter.name)) {
                    first = to_prec_connection.get(station);
                    station = precedent.get(station);
                    iter = to_prec_connection.get(station);
                    if (!precedent.containsKey(station))
                        break;
                }
                message.insert(0, "\n" + df_out.format((first.departure_time) % 2400)
                        + " -  " + df_out.format((last.arrival_time) % 2400)
                        + " : corsa " + last.name + " da " + station + " a " + arrival_station);

            }

            message.insert(0, "Viaggio da " + source + " a " + destination + "\n"
                    + "Orario di partenza: " + df_out.format(start_time % 2400) + "\n"
                    + "Orario di arrivo: " + df_out.format(distance.get(destination) % 2400));
            return message.toString();
        }
        return "";
    }
}
