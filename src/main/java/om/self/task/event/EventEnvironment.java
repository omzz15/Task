package om.self.task.event;

import java.util.LinkedList;

public abstract class EventEnvironment {
    public final LinkedList<EventContainer> events = new LinkedList<>();

    public final String name;

    public EventEnvironment(String name, EventContainer... events) {
        this.name = name;
        attachEvents(events);
    }

    public EventEnvironment(String name) {
        this.name = name;
    }

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
