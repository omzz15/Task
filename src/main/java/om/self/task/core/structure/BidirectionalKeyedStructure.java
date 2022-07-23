package om.self.task.core.structure;

import java.util.Hashtable;

public abstract class BidirectionalKeyedStructure<K, V extends Structure> extends Structure<V>{
    private final Hashtable<K, V> children = new Hashtable<>();
    private K parentKey;

    public Hashtable<K, V> getChildren() {
        return children;
    }

    public V getChild(K key){
        return children.get(key);
    }

    public void addChild(K key, V child) {
        child.onAttach(this);
        children.put(key, child);
    }
    @Override
    public void setParent(K key, V parent) {
        super.setParent(parent);
    }
}
