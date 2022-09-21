package om.self.task.core;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class EventManager {
    private final Hashtable<String, List<Runnable>> events = new Hashtable<>();

    public Hashtable<String, List<Runnable>> getEvents(){
        return events;
    }

    public void attachToEvent(String event, Runnable runnable){
        if(events.contains(event))
            events.get(event).add(runnable);
        else
            events.put(event, new LinkedList<>(List.of(runnable)));
    }

    public void attachToEvent(Enum event, Runnable runnable){
        attachToEvent(event.name(), runnable);
    }

    public boolean removeEvent(String event, Runnable runnable){
        if(!events.contains(event)) return false;
        return events.get(event).remove(runnable);
    }

    public boolean removeEvent(Enum event, Runnable runnable){
        return removeEvent(event.name(), runnable);
    }

    public void triggerEvent(String event){
        List<Runnable> eventTasks = events.get(event);
        if(eventTasks == null) return;
        for(Runnable task : eventTasks)
            task.run();
    }

    public void triggerEvent(Enum<?> event){
        triggerEvent(event.name());
    }

    public enum CommonTrigger{
        START,
        INIT,
        STOP
    }
}
