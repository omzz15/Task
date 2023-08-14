package om.self.task.event;

import om.self.structure.bidirectional.KeyedBidirectionalStructure;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static om.self.task.other.Utils.repeat;

/**
 * A way to create, store, and trigger events which are just strings that have many actions (Runnable) attached to them that get called when the event is triggered.
 */
public class EventManager extends KeyedBidirectionalStructure<String, EventManager, EventManager> {
    /**
     * A static instance of the event manager that can be used to access events from anywhere (Not really used for FTC)
     */
    private static final EventManager instance = new EventManager(null);
    /**
     * The events this manager is handling
     */
    private final ConcurrentHashMap<String, Hashtable<String, Runnable>> events = new ConcurrentHashMap<>();

    /**
     * The character used to separate the event managers when using things like {@link EventManager#getDir()}.
     * It is also used
     * to reference events in child event managers when calling {@link EventManager#triggerEvent(String)}.
     */
    public static String dirChar = "/";

    /**
     * The name of the event manager. Used for identification and to access events by directory from upper event managers
     */
    private final String name;

    /**
     * Create an event manager with a name
     * @param name the name of the event manager
     */
    public EventManager(String name) {
        this.name = name;
    }

    /**
     * Create an event manager with a name and a parent event manager
     * @param name the name of the event manager
     * @param parent the parent of the event manager
     */
    public EventManager(String name, EventManager parent) {
        this.name = name;
        attachParent(parent);
    }

    /**
     * Gets the static instance of the event manager that can be used to access events from anywhere. <br>
     * Note: Not really used for FTC
     * @return {@link #instance}
     */
    public static EventManager getInstance(){
        return instance;
    }

    /**
     * Gets all the events handled by this event manager
     * @return {@link #events}
     */
    public ConcurrentHashMap<String, Hashtable<String, Runnable>> getEvents(){
        return events;
    }

    /**
     * Gets all the actions (Runnable) attached to all the events handled by this event manager
     * @return a collection of all actions in this manager
     */
    public Collection<Runnable> getRunnables(){
        return events
                .values()
                .stream()
                .flatMap(event -> event.values().stream())
                .collect(Collectors.toList());
    }

    /**
     * Gets all the actions (Runnable) attached to a specific event
     * @param event the name of the event to get the actions from
     * @return a collection of all actions in the event (empty if event doesn't exist)
     */
    public Collection<Runnable> getRunnables(String event){
        if(!events.containsKey(event)) return Collections.emptyList();
        return events.get(event).values();
    }

    /**
     * Gets the name of this event manager
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * returns an event container for the event with the given name
     * @param event the name of the event to get the container for
     * @return an event container for the event with the given name
     */
    public EventContainer getContainer(String event){
        return new EventContainer(this, event);
    }

    /**
     * This will add a Runnable to an event
     * @param event the name of the event to attach to
     * @param runnableName the name of the runnable (this may be used later, ex: when detaching from event)
     * @param runnable the runnable that gets run by the event
     */
    public void attachToEvent(String event, String runnableName, Runnable runnable){
        if(!events.containsKey(event))
            events.put(event, new Hashtable<>());

        events.get(event).put(runnableName, runnable);
    }

    /**
     * This will add a Runnable to an event
     * @param event the name of the event to attach to
     * @param runnableName the name of the runnable(this may be used later, ex: when detaching from event)
     * @param runnable the runnable that gets run by the event
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void attachToEvent(Enum event, String runnableName, Runnable runnable){
        attachToEvent(event.name(), runnableName, runnable);
    }

    /**
     * This will add a Runnable that automatically deletes itself after the event is triggered once.
     * @param event the name of the event to attach to
     * @param runnableName the name of the runnable
     * @param runnable the runnable that gets run by the event
     */
    public void singleTimeAttachToEvent(String event, String runnableName, Runnable runnable){
        attachToEvent(event, runnableName, () -> {
            runnable.run();
            //tickets.add(() -> detachFromEvent(event, runnableName));
            detachFromEvent(event, runnableName);
        });
    }

    /**
     * This will add a runnable that automatically deletes itself after the event is triggered once.
     * @param event the name of the event to attach to
     * @param runnableName the name of the runnable
     * @param runnable the runnable that gets run by the event
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void singleTimeAttachToEvent(Enum event, String runnableName, Runnable runnable){
        singleTimeAttachToEvent(event.name(), runnableName, runnable);
    }

    /**
     * This will remove a Runnable from an event
     * @param event the name of the event to detach from
     * @param runnableName the name of the runnable(this was defined when attaching to the event)
     */
    public void detachFromEvent(String event, String runnableName){
        if(!events.containsKey(event)) return;
        events.get(event).remove(runnableName);
    }

    /**
     * This will remove a runnable from an event
     * @param event the name of the event to detach from
     * @param runnableName the name of the runnable(this was defined when attaching to the event)
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void detachFromEvent(Enum<?> event, String runnableName){
        detachFromEvent(event.name(), runnableName);
    }

    /**
     * This will remove all runnables from an event (clear it)
     * @param event the name of the event to clear
     */
    public void clearEvent(String event){
        events.remove(event);
    }

    /**
     * This will remove all runnables from an event (clear it)
     * @param event the name of the event to clear
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void clearEvent(Enum<?> event){
        clearEvent(event.name());
    }

    /**
     * This is similar to {@link EventManager#triggerEvent(String)} but it will also call triggerEvent on all children (all the way down the tree). This will trigger the event on the children before the current event manager
     * @param event the name of the event to trigger
     */
    public void triggerEventRecursively(String event){
        getChildren().forEach(child -> child.triggerEventRecursively(event));
        triggerEvent(event);
    }

    /**
     * This is similar to {@link EventManager#triggerEvent(Enum)} but it will also call triggerEvent on all children(all the way down the tree). This will trigger the event on the children before the current event manager
     * @param event the name of the event to trigger
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void triggerEventRecursively(Enum<?> event){
        triggerEventRecursively(event.name());
    }

    /**
     * This is similar to {@link EventManager#triggerEventRecursively(String)} but it will only go down the specified number of levels (children of current event manager are level 1).
     * @param event the name of the event to trigger
     * @param maxLevel the maximum number of levels to go down
     */
    public void triggerEventRecursively(String event, int maxLevel){
        if(maxLevel <= 0) return;
        getChildren().forEach(child -> child.triggerEventRecursively(event, maxLevel - 1));
        triggerEvent(event);
    }

    /**
     * This is similar to {@link EventManager#triggerEventRecursively(String)} but it will only go down the specified number of levels(children of current event manager are level 1).
     * @param event the name of the event to trigger
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void triggerEventRecursively(Enum event, int maxLevel){
        triggerEventRecursively(event.name(), maxLevel);
    }

    /**
     * Runs all runnables attached to the event.
     * If the {@link EventManager#dirChar} is in the event then it will try
     * to find the child event manager and trigger the event there.
     * If the child event manager can not be found, it will throw an exception,
     * but if the event can't be found, nothing will happen.
     * @param event the event to trigger
     */
    public void triggerEvent(String event){
        if(!event.contains(dirChar)) {
            events.getOrDefault(event, new Hashtable<>()).forEach((k,v) -> {
                try {
                    v.run();
                } catch (Exception e) {
                    throw new RuntimeException("Error while running '" + k + "' in event '" + event + "' at directory '" + getDir() + "'", e);
                }
            });
            return;
        }

        List<String> l = Arrays.stream(event.split(dirChar)).collect(Collectors.toList());

        getChild(l.remove(0)).triggerEvent(String.join(dirChar, l));
    }

    /**
     * Runs all runnables attached to the event.
     * If the {@link EventManager#dirChar} is in the event then it will try
     * to find the child event manager and trigger the event there.
     * If the child event manager can not be found, it will throw an exception,
     * but if the event can't be found, nothing will happen.
     * @param event the event to trigger
     * @deprecated the next version of this library(4.2.1) will no longer support events as enums
     */
    @Deprecated
    public void triggerEvent(Enum<?> event){
        triggerEvent(event.name());
    }

    /**
     * Returns the current directory of the event manager (basically all the parents separated by {@link EventManager#dirChar})
     * @return the current directory (includes the name of current EventManager)
     */
    public String getDir(){
        if(getParent() == null)
            return getName();

        return getParent().getDir() + dirChar + getName();
    }

    /**
     * similar to {@link EventManager#getDir()} but adds the event to the end separated by {@link EventManager#dirChar}
     * @param event the name of the event you want to directory to
     * @return the current directory of event
     */
    public String getDir(String event){
        return getDir() + dirChar + event;
    }

    /**
     * Attaches an Event manager to the current one as a child. This is the method from keyed bidirectional structure but uses the eventManager name as the child key
     * @param eventManager the event manager you want to attach as a child
     * @see KeyedBidirectionalStructure#attachChild(Object, Object)
     */
    public void attachChild(EventManager eventManager) {
        super.attachChild(eventManager.getName(), eventManager);
    }

    /**
     * Attaches an Event manager to the current one as a parent. This is the method from keyed bidirectional structure but uses the eventManager name as the parent key
     * @param eventManager the event manager you want to attach as a parent
     * @see KeyedBidirectionalStructure#attachParent(Object, Object)
     */
    public void attachParent(EventManager eventManager) {
        super.attachParent(getName(), eventManager);
    }

    /**
     * Gets the info of this manager including events and runnable as well as the info of all children
     * @param start the string to start each line with
     * @param tab the string to use as a tab
     * @return the info of this manager and all children
     */
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

    /**
     * Gets the info of this manager with {@link #getInfo(String, String)}
     * @return the info of this manager and all children
     */
    @Override
    public String toString() {
        return getInfo("","│\t");
    }
}
