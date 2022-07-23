package om.self.task.core.structure;

import java.util.LinkedList;
import java.util.List;

public abstract class BidirectionalStructure<T extends Structure> extends Structure<T>{
    private final List<T> children = new LinkedList<>();

    public List<T> getChildren() {
        return children;
    }

    public T getChild(int index){
        return children.get(index);
    }

    public void addChild(T child) {
        child.onAttach(this);
        children.add(child);
    }
}
