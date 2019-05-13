import java.util.*;

public class MinHeap {
    List<Integer> queue;
    Map<Integer, Integer> positionByNode;
    Map<Integer, Integer> distance;

    public MinHeap(Map<Integer, Integer> distance) {
        this.queue = new ArrayList<>();
        this.distance = distance;
        this.positionByNode = new HashMap<>();
    }

    int left(int i) {
        return 2 * i + 1;
    }

    int right(int i) {
        return 2 * i + 2;
    }

    int parent(int i) {
        return  (i - 1) / 2;
    }

    void add(Integer node) {
        if (positionByNode.containsKey(node)) return;

        queue.add(node);
        positionByNode.put(node, queue.size() - 1);
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

    Integer size() {
        return queue.size();
    }

    Boolean decreaseKey(Integer node, int dist) {
        if (!positionByNode.containsKey(node)) return false;

        Integer i = positionByNode.get(node);
        if(getScore(i) < dist) return false;
        distance.put(node, dist);
        bubbleUp(i);
        return true;
    }

    Integer extractMin() {
        Integer min = queue.get(0);
        positionByNode.remove(min);
        set(0, queue.get(queue.size() - 1));
        queue.remove(queue.size() - 1);
        trickeDown(0);
        return min;
    }

    private void set(Integer position, Integer node) {
        queue.set(position, node);
        positionByNode.put(node, position);
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

    private int getScore(Integer i) {
        Integer node = queue.get(i);
        return distance.get(node);
    }

    void printHeap(int pos) {
        if (pos >= size())
            return;

        printHeap(left(pos));
        printHeap(right(pos));
    }

    void printArrayHeap() {
        for(int i = 0; i < queue.size(); i++) {
            System.out.print(queue.get(i) + " ");
        }
    }

    public void swap(int a, int b) {
        Integer temp = queue.get(a);
        set(a, queue.get(b));
        set(b, temp);
    }
}
