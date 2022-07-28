package om.self.task.structure;

public abstract class Structure<T> {
    private T parent;

    public T getParent() {
        return parent;
    }

    public boolean isParentAttached(){
        return parent != null;
    }

    public void attachParent(T parent) {
        this.parent = parent;
        onAttached();
    }

    public void detachParent(){
        onDetach();
        parent = null;
    }

    protected abstract void onAttached();

    protected abstract void onDetach();
}
