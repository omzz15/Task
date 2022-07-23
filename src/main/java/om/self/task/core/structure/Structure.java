package om.self.task.core.structure;

public abstract class Structure<T> {
    private T parent;


    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        onAttach(parent);
        this.parent = parent;
    }

    public abstract void onAttach(T parent);
}
