import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AStarHeap {
  List<Integer> queue = new ArrayList<>();
  Map<Integer, Integer> positionByStation = new HashMap<>();
  Map<Integer, Long> distance;
  Map<Integer, Double> geoDistance;

  AStarHeap(Map<Integer, Long> dist, Map<Integer, Double> geodist) {
    distance = dist;
    geoDistance = geodist;
  }

  static int left(int i) {
    return 2 * i + 1;
  }

  static int right(int i) {
    return 2 * i + 2;
  }

  static int parent(int i) {
    return  (i - 1) / 2;
  }

  void add(Integer station) {
    if (positionByStation.containsKey(station)) return;

    queue.add(station);
    positionByStation.put(station, queue.size() - 1);
    bubbleUp(queue.size() - 1);
  }

  void bubbleUp(Integer i) {
    Integer p = parent(i);

    while (i > 0 && getScore(i) < getScore(p)) {
      swap(i, p);
      i = p;
      p = parent(i);
    }
  }

  Boolean decreaseKey(Integer station, Long dist, Double geoDist) {
    if (!positionByStation.containsKey(station)) return false;

    Integer i = positionByStation.get(station);
    if (getScore(i) < dist + geoDist) return false;

    distance.put(station, dist);
    geoDistance.put(station, geoDist);
    bubbleUp(i);
    return true;
  }

  Integer extractMin() {
    Integer min = queue.get(0);
    positionByStation.remove(min);
    set(0, queue.get(queue.size() - 1));
    queue.remove(queue.size() - 1);
    trickeDown(0);
    
    return min;
  }

  void trickeDown(Integer i) {
    Integer l = left(i);
    Integer r = right(i);
    Integer n = queue.size();
    Integer smallest = i;

    if (l < n && getScore(l) < getScore(i)) {
      smallest = l;
    }

    if (r < n && getScore(r) < getScore(smallest)) {
      smallest = r;
    }

    if (smallest != i) {
      swap(i, smallest);
      trickeDown(smallest);
    }
  }

  Integer size() {
    return queue.size();
  }

  private double getScore(Integer i) {
    Integer station = queue.get(i);

    return distance.get(station) + geoDistance.get(station);
  }

  private void swap(Integer a, Integer b) {
    Integer temp = queue.get(a);
    set(a, queue.get(b));
    set(b, temp);
  }

  private void set(Integer position, Integer station) {
    queue.set(position, station);
    positionByStation.put(station, position);
  }
}
