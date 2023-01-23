package om.self.task.event;

import java.util.LinkedList;

/**
 * A way to listen to multiple events
 */
public abstract class EventEnvironment {
    public final LinkedList<EventContainer> events = new LinkedList<>();

    public final String name;

    /**
     * create an environment with some events already registered
     * @param name the name of the environment
     * @param events the events this environment should be registered to
     */
    public EventEnvironment(String name, EventContainer... events) {
        this.name = name;
        attachEvents(events);
    }

    /**
     * creates an empty environment
     * @param name the name of the environment
     */
    public EventEnvironment(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public LinkedList<EventContainer> getEvents(){
        return events;
    }

    public void attachEvents(EventContainer... events){
        for (EventContainer event : events) {
            event.singleTimeAttach("trigger for environment - " + name, () -> onTrigger(event));
            this.events.add(event);
        }
    }

    public void detachEvents(EventContainer... events){
        for (EventContainer event : events) {
            event.detach("trigger for environment - " + name);
            this.events.remove(event);
        }
    }

    public void clearEnvironment(){
        detachEvents(events.toArray(new EventContainer[0]));
    }

    public abstract void onTrigger(EventContainer event);
}
