package om.self.task.core;

import om.self.structure.bidirectional.KeyedBidirectionalStructure;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.repeat;

public class EventManager extends KeyedBidirectionalStructure<String, EventManager, EventManager> {
    private static final EventManager instance = new EventManager(null);
    private final Hashtable<String, Hashtable<String, Runnable>> events = new Hashtable<>();
    private static final List<Runnable> tickets = new LinkedList<>();

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
                .collect(Collectors.toList());
    }

    public Collection<Runnable> getRunnables(String event){
        if(!events.containsKey(event)) return Collections.emptyList();
        return events.get(event).values();
    }

    public String getName() {
        return name;
    }

    public void attachToEvent(String event, String runnableName, Runnable runnable){
        if(!events.containsKey(event))
            events.put(event, new Hashtable<>());

        events.get(event).put(runnableName, runnable);
    }

    public void attachToEvent(Enum event, String runnableName, Runnable runnable){
        attachToEvent(event.name(), runnableName, runnable);
    }

    public void singleTimeAttachToEvent(String event, String runnableName, Runnable runnable){
        attachToEvent(event, runnableName, () -> {
            runnable.run();
            tickets.add(() -> detachFromEvent(event, runnableName));
        });
    }

    /**
     * this will add a runnable that automatically deletes itself after the event is triggered once.
     * @param event
     * @param runnableName
     * @param runnable
     */
    public void singleTimeAttachToEvent(Enum event, String runnableName, Runnable runnable){
        singleTimeAttachToEvent(event.name(), runnableName, runnable);
    }

    public void detachFromEvent(String event, String runnableName){
        if(!events.containsKey(event)) return;
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
        tickets.forEach(Runnable::run);
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

    public String getInfo(String start, String tab){
        StringBuilder str = new StringBuilder(start);
        str.append(getName()).append(": EventManager").append("(dir: ").append(getDir()).append(")\n");
        for (Map.Entry<String, Hashtable<String, Runnable>> event: events.entrySet()) {
            str.append(start).append(tab).append(event.getKey()).append(": Event\n");
            for(String runnable : event.getValue().keySet())
                str.append(start).append(repeat(tab, 2)).append(runnable).append(": Runnable\n");
        }
        for (EventManager em: getChildren()) {
            str.append(em.getInfo(start + tab, tab));
        }

        return str.toString();
    }

    @Override
    public String toString() {
        return getInfo("","│\t");
    }

    public enum CommonTrigger{
        START,
        INIT,
        STOP
    }
}
