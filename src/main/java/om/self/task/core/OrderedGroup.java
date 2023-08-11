package om.self.task.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An extension of {@link Group} that runs the runnables in the order they were started (unless otherwise specified)
 */
public class OrderedGroup extends Group{
    /**
     * The default value for {@link CommandVars#allowMultiRun} if it is not specified.
     */
    public boolean allowMultiRunDefault = true;

    /**
     * A list of all active runnables in the order they were started.
     */
    private final CopyOnWriteArrayList<Runnable> orderedActiveRunnable = new CopyOnWriteArrayList<>();

    /**
     * Creates a new OrderedGroup with the given name and no parent.
     * @param name the name of the new OrderedGroup
     */
    public OrderedGroup(String name) {
        super(name);
    }

    /**
     * Creates a new OrderedGroup with the given name and parent.
     * @param name the name of the new OrderedGroup
     * @param parent the parent of the new OrderedGroup
     */
    public OrderedGroup(String name, Group parent) {
        super(name, parent);
    }

    /**
     * Creates a new OrderedGroup with the given name, and parent that is attached with a custom parent key.
     * @param name the name of the new OrderedGroup
     * @param parentKey the parentKey of the new OrderedGroup
     * @param parent the parent of the new OrderedGroup
     */
    public OrderedGroup(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * Gets a list of all active runnables in the order they were started.
     * @return {@link #orderedActiveRunnable}
     */
    public List<Runnable> getOrderedActiveRunnable() {
        return orderedActiveRunnable;
    }

    /**
     * Adds a runnable to {@link #orderedActiveRunnable} so it can be run
     * @param key the key associated with the runnable
     * @param runnable the runnable to add
     * @param args the arguments to pass to the runnable (mainly the {@link CommandVars#location} arg)
     */
    @Override
    protected void addToActive(String key, Runnable runnable, Map.Entry<String, Object>... args) {
        super.addToActive(key, runnable, args);


        if(!getArg(CommandVars.allowMultiRun, allowMultiRunDefault, args) && orderedActiveRunnable.contains(runnable)) return;
        Optional<Object> location = getArg(CommandVars.location, args);
        if(location.isPresent()) orderedActiveRunnable.add((int)location.get(), runnable);
        else orderedActiveRunnable.add(runnable);
    }

    /**
     * Removes a runnable from {@link #orderedActiveRunnable} so it can no longer be run
     * @param key the key associated with the runnable
     * @param args the arguments to pass to the runnable
     */
    @Override
    protected void removeFromActive(String key, Map.Entry<String, Object>... args) {
        while (orderedActiveRunnable.contains(getActiveRunnable(key)))
            orderedActiveRunnable.remove(getActiveRunnable(key));
        super.removeFromActive(key, args);
    }

    /**
     * Runs {@link Group#runQueuedActions()} then all the runnables in {@link #orderedActiveRunnable}
     */
    @Override
    public void run(){
        runQueuedActions();
        for(int i = 0; i < orderedActiveRunnable.size(); i++){
            try {
                orderedActiveRunnable.get(i).run();
            } catch (Exception e){
                throw new RuntimeException("Error running '" + orderedActiveRunnable.get(i) + "' at index " + i + " in ordered group '" + getName() + "'", e);
            }
        }
    }
}
