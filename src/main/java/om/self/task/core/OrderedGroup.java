package om.self.task.core;

import java.util.LinkedList;
import java.util.List;

public class OrderedGroup extends Group{
    public boolean allowMultirun = true;

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

    public List<Runnable> getOrderedActiveRunnable() {
        return orderedActiveRunnable;
    }

    @Override
    protected void addToActive(String key, Runnable runnable, Object... args) {
        super.addToActive(key, runnable, args);
        if(!allowMultirun && orderedActiveRunnable.contains(runnable)) return;
        try{
            orderedActiveRunnable.add((int) args[0], runnable);
        } catch (Exception e) {
            orderedActiveRunnable.add(runnable);
        }
    }

    @Override
    protected void removeFromActive(String key, Object... args) {
        orderedActiveRunnable.remove(getActiveRunnable(key));
        super.removeFromActive(key, args);
    }

    @Override
    public void run(){
        runQueuedActions();
        orderedActiveRunnable.forEach(Runnable::run);
    }
}
