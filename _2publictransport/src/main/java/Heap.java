import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Heap {
  List<Integer> queue = new ArrayList<>();
  Map<Integer, Integer> positionByStation = new HashMap<>();
  Map<Integer, Long> distance;

  Heap(Map<Integer, Long> dist) {
    distance = dist;
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

    while (i > 0 && distance.get(queue.get(i)) < distance.get(queue.get(p))) {
      swap(i, p);
      i = p;
      p = parent(i);
    }
  }

  Boolean decreaseKey(Integer station, Long dist) {
    if (!positionByStation.containsKey(station)) return false;

    Integer i = positionByStation.get(station);
    if (distance.get(station) < dist) return false;

    distance.put(station, dist);
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

    if (l < n && distance.get(queue.get(l)) < distance.get(queue.get(i))) {
      smallest = l;
    }

    if (r < n && distance.get(queue.get(r)) < distance.get(queue.get(smallest))) {
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
