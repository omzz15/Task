package om.self.task.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderedGroup extends Group{
    public boolean allowMultiRunDefault = true;

    private final CopyOnWriteArrayList<Runnable> orderedActiveRunnable = new CopyOnWriteArrayList<>();

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
    protected void addToActive(String key, Runnable runnable, Map.Entry<String, Object>... args) {
        super.addToActive(key, runnable, args);


        if(!getArg(CommandVars.allowMultiRun, allowMultiRunDefault, args) && orderedActiveRunnable.contains(runnable)) return;
        Optional<Object> location = getArg(CommandVars.location, args);
        if(location.isPresent()) orderedActiveRunnable.add((int)location.get(), runnable);
        else orderedActiveRunnable.add(runnable);
    }

    @Override
    protected void removeFromActive(String key, Map.Entry<String, Object>... args) {
        while (orderedActiveRunnable.contains(getActiveRunnable(key)))
            orderedActiveRunnable.remove(getActiveRunnable(key));
        super.removeFromActive(key, args);
    }

    @Override
    public void run(){
        runQueuedActions();
        orderedActiveRunnable.forEach(Runnable::run);
    }
}
