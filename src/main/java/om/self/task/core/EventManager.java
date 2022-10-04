package om.self.task.core;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class EventManager {
    private static final EventManager instance = new EventManager();
    private final Hashtable<String, List<Runnable>> events = new Hashtable<>();


    public static EventManager getInstance(){
        return instance;
    }

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

    public void detachFromEvent(String event, Runnable runnable){
        if(!events.contains(event)) return;
        events.get(event).remove(runnable);
    }

    public void detachFromEvent(Enum event, Runnable runnable){
        detachFromEvent(event.name(), runnable);
    }

    public void clearEvent(String event){
        events.remove(event);
    }

    public void clearEvent(Enum event){
        clearEvent(event.name());
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
