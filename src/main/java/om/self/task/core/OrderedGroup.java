package om.self.task.core;

import java.util.LinkedList;
import java.util.List;

public class OrderedGroup extends Group{
    private final List<Runnable> orderedActiveRunnable = new LinkedList<>();

    public OrderedGroup(String name) {
        super(name);
    }

    public OrderedGroup(String name, Group parent) {
        super(name, parent);
    }

    public OrderedGroup(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    @Override
    protected boolean startRunnable(String key) {
        if(super.startRunnable(key)){
            orderedActiveRunnable.add(getActiveRunnable(key));
            return true;
        }
        return false;
    }

    @Override
    public void run(){
        runQueuedActions();
        orderedActiveRunnable.forEach(Runnable::run);
    }
}
