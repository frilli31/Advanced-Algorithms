import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralTree {
    public static Node createTree(List<Node> nodes) {

        Map<String, Node> mapTmp = new HashMap<>();

        for (Node current : nodes) {
            mapTmp.put(current.getId(), current);
        }

        for (Node current : nodes) {
            String parentId = current.getParentId();

            if (parentId != null) {
                Node parent = mapTmp.get(parentId);
                if (parent != null) {
                    current.setParent(parent);
                    parent.addChild(current);
                    mapTmp.put(parentId, parent);
                    mapTmp.put(current.getId(), current);
                }
            }

        }

        Node root = null;
        for (Node node : mapTmp.values()) {
            if (node.getParent() == null) {
                root = node;
                break;
            }
        }

        return root;
    }

    public static List<Node> flatten(Node node) {

        if (node == null) {
            return null;
        }

        List<Node> flatList = new ArrayList<>();

        Deque<Node> q = new ArrayDeque<>();
        q.addLast(node);

        while (!q.isEmpty()) {
            Node n = q.removeLast();
            flatList.add(new Node(n.getValue(), n.getId(), n.getParentId()));
            List<Node> children = n.getChildren();
            if (children != null) {
                for (Node child : children) {
                    q.addLast(child);
                }
            }
        }

        return flatList;
    }
}
