package om.self.task.event;

import java.util.*;

/**
 * An extension of {@link EventManager} that allows for actions to be run in the order they were attached to an event.
 */
public class OrderedEventManager extends EventManager{
    /**
     * A map of all runnables attached to each event in the order they were attached.
     */
    private final Hashtable<String, List<Runnable>> orderedEvents = new Hashtable<>();

    /**
     * Creates a new OrderedEventManager with the given name and no parent.
     * @param name the name of this OrderedEventManager
     */
    public OrderedEventManager(String name) {
        super(name);
    }

    /**
     * Creates a new OrderedEventManager with the given name and parent.
     * @param name the name of this OrderedEventManager
     * @param parent the parent of this OrderedEventManager
     */
    public OrderedEventManager(String name, EventManager parent) {
        super(name, parent);
    }

    /**
     * Gets all runnables attached to the given event in the order they were attached.
     * @param event the name of the event to get the actions from
     * @return runnable attached to the given event in the order they were attached
     */
    @Override
    public Collection<Runnable> getRunnables(String event){
        if(!orderedEvents.containsKey(event)) return Collections.emptyList();
        return orderedEvents.get(event);
    }

    /**
     * Attaches a Runnable to an event
     * @param event the name of the event to attach to
     * @param runnableName the name of the runnable (this may be used later, ex: when detaching from event)
     * @param runnable the runnable that gets run by the event
     */
    @Override
    public void attachToEvent(String event, String runnableName, Runnable runnable) {
        if(!getEvents().containsKey(event)) {
            getEvents().put(event, new Hashtable<>());
            orderedEvents.put(event, new LinkedList<>());
        }

        getEvents().get(event).put(runnableName, runnable);
        orderedEvents.get(event).add(runnable);
    }

    /**
     * Attaches a Runnable to an event at a specific location
     * @param event the name of the event to attach to
     * @param runnableName the name of the runnable (this may be used later, ex: when detaching from event)
     * @param runnable the runnable that gets run by the event
     * @param location the location to add the runnable at
     */
    public void attachToEvent(String event, String runnableName, Runnable runnable, int location){
        if(!getEvents().containsKey(event)) {
            getEvents().put(event, new Hashtable<>());
            orderedEvents.put(event, new LinkedList<>());
        }

        getEvents().get(event).put(runnableName, runnable);
        orderedEvents.get(event).add(location, runnable);
    }

    /**
     * Detaches a Runnable from an event
     * @param event the name of the event to detach from
     * @param runnableName the name of the runnable(this was defined when attaching to the event)
     */
    @Override
    public void detachFromEvent(String event, String runnableName) {
        if(!getEvents().containsKey(event)) return;
        orderedEvents.get(event).remove(getEvents().get(event).remove(runnableName));
    }

    /**
     * Clears all runnables from an event
     * @param event the name of the event to clear
     */
    @Override
    public void clearEvent(String event) {
        super.clearEvent(event);
        orderedEvents.remove(event);
    }
}
