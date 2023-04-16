package om.self.task.event;

import java.util.Hashtable;

public class WaitForAllEvents extends EventEnvironment {

    private final Hashtable<EventContainer, Boolean> eventTriggerTable = new Hashtable<>();
    private final EventContainer output;

    public WaitForAllEvents(String name, EventContainer outputEvent, EventContainer... events) {
        super(name);
        attachEvents(events);
        output = outputEvent;
    }

    public WaitForAllEvents(String name, EventContainer outputEvent) {
        super(name);
        output = outputEvent;
    }

    @Override
    public void attachEvents(EventContainer... events) {
        super.attachEvents(events);
        for (EventContainer event : events) {
            eventTriggerTable.put(event, false);
        }
    }

    @Override
    public void detachEvents(EventContainer... events) {
        super.detachEvents(events);
        for (EventContainer event : events) {
            eventTriggerTable.remove(event);
        }

        if(!eventTriggerTable.containsValue(false))
            triggerOut();
    }

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

    @Override
    public void onTrigger(EventContainer event) {
        eventTriggerTable.put(event, true);
        if(!eventTriggerTable.containsValue(false))
            triggerOut();
    }

    private void triggerOut(){
        output.trigger();
        clearEnvironment();
    }
}
