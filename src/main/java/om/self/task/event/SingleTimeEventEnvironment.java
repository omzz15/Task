package om.self.task.event;

/**
 * An extention of {@link EventEnvironment} that automatically removes events once triggred.
 */
public abstract class SingleTimeEventEnvironment extends EventEnvironment {
    /**
     * create an environment with some events already registered
     * @param name the name of the environment
     * @param events the events this environment should be registered to
     */
    public SingleTimeEventEnvironment(String name, EventContainer... events) {
        super(name, events);
    }

    /**
     * creates an empty environment
     * @param name the name of the environment
     */
    public SingleTimeEventEnvironment(String name) {
        super(name);
    }

    /**
     * attach events to this environment by creating a trigger for each event that automatically remove themselves once triggred
     * @param events the events to attach
     */
    public void attachEvents(EventContainer... events){
        for (EventContainer event : events) {
            event.singleTimeAttach("trigger for environment - " + name, () -> trigger(event));
            this.events.add(event);
        }
    }

    private void trigger(EventContainer event){
        onTrigger(event);
        events.remove(event);
    }
}
