package om.self.task.core;

import om.self.task.event.EventManager;

/**
 * An extension of {@link Group} that allows you to set a function to run while it waits for an event.
 */
public class GroupEx extends Group{
    /**
     * The function that will be run when {@link #run} is called. (Usually just {@link #getBaseRunFunction()})
     */
    private Runnable runFunction = getBaseRunFunction();

    /**
     * Creates a new GroupEx with the given name and no parent.
     * @param name the name of the new GroupEx
     */
    public GroupEx(String name) {
        super(name);
    }

    /**
     * Creates a new GroupEx with the given name and parent.
     * @param name the name of the new GroupEx
     * @param parent the parent of the new GroupEx
     */
    public GroupEx(String name, Group parent) {
        super(name, parent);
    }

    /**
     * Creates a new GroupEx with the given name, and parent that is attached with a custom parent key.
     * @param name the name of the new GroupEx
     * @param parentKey the parentKey of the new GroupEx
     * @param parent the parent of the new GroupEx
     */
    public GroupEx(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * Gets the default function that will be run when {@link #run} is called.
     * This function will run all queued actions and then all active runnables.
     * @return the default run function
     */
    private Runnable getBaseRunFunction(){
        return () -> {
            while(!queuedGroupActions.isEmpty()) queuedGroupActions.removeFirst().run();
            activeRunnables.forEach((k, v) -> v.run());
        };
    }

    /**
     * Runs the specified runnable until an event is fired then resumes normal operation.
     * @param event the event to wait for
     * @param manager the event manager to listen to
     * @param runnable the runnable to run while waiting for the event
     */
    public void waitForEvent(String event, EventManager manager, Runnable runnable){
        runFunction = runnable;
        manager.singleTimeAttachToEvent(event, "set run in group - " + getName(), () -> runFunction = getBaseRunFunction());
    }

    /**
     * Runs the {@link #runFunction}
     */
    @Override
    public void run(){
        runFunction.run();
    }
}
