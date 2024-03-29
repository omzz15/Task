package om.self.task.core;

import om.self.structure.bidirectional.KeyedBidirectionalStructure;

import java.util.*;

public class EventManager extends KeyedBidirectionalStructure<String, EventManager, EventManager> {
    private static final EventManager instance = new EventManager(null);
    private final Hashtable<String, Hashtable<String, Runnable>> events = new Hashtable<>();

    private final String name;

    public EventManager(String name) {
        this.name = name;
    }

    public EventManager(String name, EventManager parent) {
        this.name = name;
        attachParent(parent);
    }

    public static EventManager getInstance(){
        return instance;
    }

    public Hashtable<String, Hashtable<String, Runnable>> getEvents(){
        return events;
    }

    public Collection<Runnable> getRunnables(){
        return events
                .values()
                .stream()
                .flatMap(event -> event.values().stream())
                .toList();
    }

    public Collection<Runnable> getRunnables(String event){
        if(!events.containsKey(event)) return Collections.emptyList();
        return events.get(event).values();
    }

    public String getName() {
        return name;
    }

    public void attachToEvent(String event, String runnableName, Runnable runnable){
        if(!events.contains(event))
            events.put(event, new Hashtable<>());

        events.get(event).put(runnableName, runnable);
    }

    public void attachToEvent(Enum event, String runnableName, Runnable runnable){
        attachToEvent(event.name(), runnableName, runnable);
    }

    public void detachFromEvent(String event, String runnableName){
        if(!events.contains(event)) return;
        events.get(event).remove(runnableName);
    }

    public void detachFromEvent(Enum<?> event, String runnableName){
        detachFromEvent(event.name(), runnableName);
    }

    public void clearEvent(String event){
        events.remove(event);
    }

    public void clearEvent(Enum<?> event){
        clearEvent(event.name());
    }

    public void triggerEventRecursively(String event){
        getChildren().forEach(child -> child.triggerEventRecursively(event));

        triggerEvent(event);
    }

    public void triggerEventRecursively(Enum<?> event){
        triggerEventRecursively(event.name());
    }

    public void triggerEvent(String event){
        getRunnables(event).forEach(Runnable::run);
    }

    public void triggerEvent(Enum<?> event){
        triggerEvent(event.name());
    }

    public String getDir(){
        if(getParent() == null)
            return getName();

        return getParent().getDir() + "/" + getName();
    }

    public void attachChild(EventManager eventManager) {
        super.attachChild(eventManager.getName(), eventManager);
    }

    public void attachParent(EventManager eventManager) {
        super.attachParent(getName(), eventManager);
    }

    public enum CommonTrigger{
        START,
        INIT,
        STOP
    }
}
