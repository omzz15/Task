package om.self.task.event;

import java.util.Objects;

/**
 * An easier way to reference and store a specific event (good for cases where you need to deal with many events from different managers, ex: method parameters)
 */
public class EventContainer {
    /**
     * The {@link EventManager} that this event is attached to
     */
    public final EventManager manager;
    /**
     * The name of the event this container is storing
     */
    public final String event;

    /**
     * create an event container for a specific event
     * @param manager the {@link EventManager} that the event is linked to
     * @param event the name of the event you want to reference
     */
    public EventContainer(EventManager manager, String event) {
        this.manager = manager;
        this.event = event;
    }

    /**
     * trigger the event
     * @see EventManager#triggerEvent(String)
     */
    public void trigger(){
        manager.triggerEvent(event);
    }

    /**
     * attach another runnable to an event
     * @param runnableName the name of the runnable (must be unique) (used in info and for removing the runnable)
     * @param runnable the runnable to run when event is triggered
     * @see EventManager#attachToEvent(String, String, Runnable)
     */
    public void attach(String runnableName, Runnable runnable){
        manager.attachToEvent(event, runnableName, runnable);
    }

    /**
     * attach another runnable to an event that will immediately detach
     * @param runnableName the name of the runnable (must be unique) (used in info and for removing the runnable)
     * @param runnable the runnable to run when event is triggered
     * @see EventManager#singleTimeAttachToEvent(String, String, Runnable)
     */
    public void singleTimeAttach(String runnableName, Runnable runnable){
        manager.singleTimeAttachToEvent(event, runnableName, runnable);
    }

    /**
     * detach a Runnable from the event
     * @param runnableName the name of the runnable to detach(set during {@link #attach(String, Runnable)})
     * @see EventManager#detachFromEvent(String, String)
     */
    public void detach(String runnableName){
        manager.detachFromEvent(event, runnableName);
    }

    /**
     * ensure that even different EventContainer objects are equal if the manager and event are the same.
     * @param o the object to check
     * @return whether the EventContainers store the same information (the instances don't need to match)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventContainer that = (EventContainer) o;
        return Objects.equals(manager, that.manager) && Objects.equals(event, that.event);
    }

    /**
     * hash code
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(manager, event);
    }
}
