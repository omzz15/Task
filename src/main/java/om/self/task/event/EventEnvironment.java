package om.self.task.event;

import java.util.LinkedList;

/**
 * A way to group and listen to multiple events
 */
public abstract class EventEnvironment {
    /**
     * the events this environment is listening to
     */
    public final LinkedList<EventContainer> events = new LinkedList<>();

    /**
     * the name of the environment
     */
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
     * get the events this environment is listening to
     * @return {@link #events}
     */
    public LinkedList<EventContainer> getEvents(){
        return events;
    }

    /**
     * attach events to this environment by creating a trigger for each event
     * @param events the events to attach
     */
    public void attachEvents(EventContainer... events){
        for (EventContainer event : events) {
            event.singleTimeAttach("trigger for environment - " + name, () -> onTrigger(event));
            this.events.add(event);
        }
    }

    /**
     * detach events from this environment and removes their triggers
     * @param events the events to detach
     */
    public void detachEvents(EventContainer... events){
        for (EventContainer event : events) {
            event.detach("trigger for environment - " + name);
            this.events.remove(event);
        }
    }

    /**
     * clear all events from this environment
     */
    public void clearEnvironment(){
        detachEvents(events.toArray(new EventContainer[0]));
    }

    /**
     * This is called when one of the events in this environment is triggered
     * @param event the event that was triggered
     */
    public abstract void onTrigger(EventContainer event);
}
