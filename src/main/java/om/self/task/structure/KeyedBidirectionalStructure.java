package om.self.task.structure;

import java.util.Hashtable;

public abstract class KeyedBidirectionalStructure<K, PARENT, CHILD extends KeyedStructure<K, KeyedBidirectionalStructure>> extends KeyedStructure<K, PARENT>{
    Hashtable<K, CHILD> children;

    public Hashtable<K, CHILD> getChildren() {
        return children;
    }

    public CHILD getChild(K key){
        return children.get(key);
    }

    public void addChild(K childKey, CHILD child) {
        child.attachParent(childKey, this);
        children.put(childKey, child);
    }

    public void detachChild(K key){
        CHILD child = children.remove(key);
        if(child != null)
            child.detachParent();
    }

    public void detachChild(CHILD child){
        if(children.remove(child.getParentKey(), child))
            child.detachParent();
    }

    public void attachParent(K parentKey, PARENT parent) {
        try{
            ((KeyedBidirectionalStructure)parent).addChild(parentKey, this);
        }catch (Exception ignored){
            super.attachParent(parentKey, parent);
        }
    }

    @Override
    public void detachParent() {
        try{
            ((KeyedBidirectionalStructure)getParent()).detachChild(getParentKey());
        }catch (Exception ignored){
            super.detachParent();
        }
    }
}