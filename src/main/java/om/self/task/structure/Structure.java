package om.self.task.structure;

public abstract class Structure<T> {
    private T parent;

    public T getParent() {
        return parent;
    }

    public void attachParent(T parent) {
        this.parent = parent;
        onAttached();
    }

    public void detachParent(){
        onDetach();
        parent = null;
    }

    public abstract void onAttached();

    public abstract void onDetach();
}
