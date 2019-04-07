import java.util.ArrayList;
import java.util.List;

public class Heap {
  List<HeapItem> queue = new ArrayList<>();

  Heap() {}

  static int left(int i) {
    return 2 * i + 1;
  }

  static int right(int i) {
    return 2 * i + 2;
  }

  static int parent(int i) {
    return  (i - 1) / 2;
  }

  void add(HeapItem x) {
    queue.add(x);
    bubbleUp(queue.size());
  }

  void bubbleUp(Integer i) {
    Integer p = parent(i);

    while (i > 0 && queue.get(i).distance < queue.get(p).distance) {
      swap(i, p);
      i = p;
      p = parent(i);
    }
  }

  Boolean decreaseKey(Integer i, Long distance) {
    if (queue.get(i).distance < distance) return false;

    queue.get(i).distance = distance;
    bubbleUp(i);
    return true;
  }

  HeapItem extractMin() {
    HeapItem min = queue.get(0);
    queue.set(0, queue.get(queue.size() - 1));
    queue.remove(queue.size() - 1);
    
    return min;
  }

  void trickeDown(Integer i) {
    Integer l = left(i);
    Integer r = right(i);
    Integer n = queue.size();
    Integer smallest = i;

    if (l < n && queue.get(l).distance < queue.get(i).distance) {
      smallest = l;
    }

    if (r < n && queue.get(r).distance < queue.get(smallest).distance) {
      smallest = r;
    }

    if (smallest != i) {
      swap(i, smallest);
      trickeDown(smallest);
    }
  }

  private void swap(Integer a, Integer b) {
    HeapItem temp = queue.get(a);
    queue.set(a, queue.get(b));
    queue.set(b, temp);
  }
}
