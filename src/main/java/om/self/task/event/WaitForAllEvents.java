package om.self.task.event;

import java.util.Hashtable;

/**
 * An extension of {@link EventEnvironment} that waits for all events to be triggered before triggering its own event
 */
public class WaitForAllEvents extends EventEnvironment {
    /**
     * A table of all events and whether they have been triggered
     */
    private final Hashtable<EventContainer, Boolean> eventTriggerTable = new Hashtable<>();
    /**
     * The event to trigger when all events have been triggered
     */
    private EventContainer output;

    /**
     * Creates a new WaitForAllEvents with the given name, output event, and events
     * @param name the name of this
     * @param outputEvent the event to trigger when all events have been triggered
     * @param events the events to wait for
     */
    public WaitForAllEvents(String name, EventContainer outputEvent, EventContainer... events) {
        super(name);
        attachEvents(events);
        output = outputEvent;
    }

    /**
     * Creates a new WaitForAllEvents with the given name and output event
     * @param name the name of this
     * @param outputEvent the event to trigger when all events have been triggered
     */
    public WaitForAllEvents(String name, EventContainer outputEvent) {
        super(name);
        output = outputEvent;
    }

    /**
     * Creates a new WaitForAllEvents with the given name and events
     * @param name the name of this
     * @param events the events to wait for
     */
    public WaitForAllEvents(String name, EventContainer... events) {
        super(name);
        attachEvents(events);
    }

    /**
     * Gets the output event that will be triggered when all events have been triggered
     * @return {@link #output}
     */
    public EventContainer getOutput() {
        return output;
    }

    /**
     * Sets the output event that will be triggered when all events have been triggered
     * @param output the new output event
     */
    public void setOutput(EventContainer output) {
        this.output = output;
    }

    /**
     * attach events to this environment by creating a trigger for each event
     * @param events the events to attach
     */
    @Override
    public void attachEvents(EventContainer... events) {
        super.attachEvents(events);
        for (EventContainer event : events) {
            eventTriggerTable.put(event, false);
        }
    }

    /**
     * detach events from this environment and removes their triggers
     * @param events the events to detach
     */
    @Override
    public void detachEvents(EventContainer... events) {
        super.detachEvents(events);
        for (EventContainer event : events) {
            eventTriggerTable.remove(event);
        }

        if(!eventTriggerTable.containsValue(false))
            triggerOut();
    }

    /**
     * clears the environment by detaching all events and removing their triggers
     */
    public void clearEnvironment(){
        for (EventContainer event : events) {
            event.detach("trigger for environment - " + name);
        }
        for (EventContainer event : events) {
            eventTriggerTable.remove(event);
        }

        events.clear();
        eventTriggerTable.clear();
    }

    /**
     * Called when an event is triggered. <br>
     * IMPORTANT: This method should NOT be called manually
     * @param event the event that was triggered
     */
    @Override
    public void onTrigger(EventContainer event) {
        eventTriggerTable.put(event, true);
        if(!eventTriggerTable.containsValue(false))
            triggerOut();
    }

    /**
     * Triggers the output event and clears the environment
     */
    private void triggerOut(){
        output.trigger();
        clearEnvironment();
    }
}
