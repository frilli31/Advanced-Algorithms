import java.util.ArrayList;
import java.util.List;

public class Node {

    private String id;
    private String parentId;

    private String value;
    private Node parent;

    private List<Node> children;

    public Node(String value, String childId, String parentId) {
        this.value = value;
        this.id = childId;
        this.parentId = parentId;
        this.children = new ArrayList<>();
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        if (!this.children.contains(child) && child != null)
            this.children.add(child);
    }

    @Override
    public String toString() {
        return "Node [id=" + id + ", parentId=" + parentId + ", value=" + value + ", children="
                + children + "]";
    }
}
