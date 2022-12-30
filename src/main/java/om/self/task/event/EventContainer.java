package om.self.task.event;

import java.util.Objects;

/**
 * An easier way to reference specific events(good for cases where you need to deal with many events from different managers, ex: methods parameters)
 */
public class EventContainer {
    public final EventManager manager;
    public final String event;

    public EventContainer(EventManager manager, String event) {
        this.manager = manager;
        this.event = event;
    }

    public void trigger(){
        manager.triggerEvent(event);
    }

    public void attach(String runnableName, Runnable runnable){
        manager.attachToEvent(event, runnableName, runnable);
    }

    public void singleTimeAttach(String runnableName, Runnable runnable){
        manager.singleTimeAttachToEvent(event, runnableName, runnable);
    }

    public void detach(String runnableName){
        manager.detachFromEvent(event, runnableName);
    }

    /**
     * ensure that even different EventContainer objects are equal if the manager and event are the same.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventContainer that = (EventContainer) o;
        return Objects.equals(manager, that.manager) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, event);
    }
}
