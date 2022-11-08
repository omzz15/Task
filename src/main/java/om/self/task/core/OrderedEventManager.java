package om.self.task.core;

import java.util.*;

public class OrderedEventManager extends EventManager{
    private final Hashtable<String, List<Runnable>> orderedEvents = new Hashtable<>();

    public OrderedEventManager(String name) {
        super(name);
    }

    public OrderedEventManager(String name, EventManager parent) {
        super(name, parent);
    }

    @Override
    public Collection<Runnable> getRunnables(String event){
        if(!orderedEvents.containsKey(event)) return Collections.emptyList();
        return orderedEvents.get(event);
    }

    @Override
    public void attachToEvent(String event, String runnableName, Runnable runnable) {
        if(!getEvents().containsKey(event)) {
            getEvents().put(event, new Hashtable<>());
            orderedEvents.put(event, new LinkedList<>());
        }

        getEvents().get(event).put(runnableName, runnable);
        orderedEvents.get(event).add(runnable);
    }

    public void attachToEvent(String event, String runnableName, Runnable runnable, int location){
        if(!getEvents().containsKey(event)) {
            getEvents().put(event, new Hashtable<>());
            orderedEvents.put(event, new LinkedList<>());
        }

        getEvents().get(event).put(runnableName, runnable);
        orderedEvents.get(event).add(location, runnable);
    }

    @Override
    public void detachFromEvent(String event, String runnableName) {
        if(!getEvents().containsKey(event)) return;
        orderedEvents.get(event).remove(getEvents().get(event).remove(runnableName));
    }

    @Override
    public void clearEvent(String event) {
        super.clearEvent(event);
        orderedEvents.remove(event);
    }
}
