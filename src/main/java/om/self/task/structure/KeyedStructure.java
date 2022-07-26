package om.self.task.structure;

public abstract class KeyedStructure <K, V> extends  Structure<V>{
    private K parentKey;

    @Override
    public void attachParent(V parent) {
        throw new UnsupportedOperationException("Keyed Structures require both the parent and parent key to be set! Use attachParent(K parentKey, V parent) instead");
    }

    public void attachParent(K parentKey, V parent){
        this.parentKey = parentKey;
        attachParent(parent);
    }

    @Override
    public void detachParent(){
        super.detachParent();
        this.parentKey = null;
    }

    public K getParentKey() {
        return parentKey;
    }
}
