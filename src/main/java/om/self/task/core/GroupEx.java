package om.self.task.core;

import om.self.task.event.EventManager;

/**
 * An extension of {@link Group} that allows you to set a function to run while it waits for an event.
 */
public class GroupEx extends Group{
    /**
     * The function that will be run when {@link #run} is called. (Usually just {@link #generateRunFunction()} unless you are waiting for an event)
     */
    private Runnable runFunction = generateRunFunction();
    /**
     * This is the default function that will be called when {@link #run} is called if there are no active runnables.
     * <br>
     * Note: This is only used if it is not null.
     */
    private Runnable baseFunction;

    /**
     * A flag to indicate if the group is waiting for an event before resuming normal function.
     */
    private boolean waitingForEvent = false;

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
     * Gets the function that will be run when {@link #run} is called.
     * @return {@link #runFunction}
     */
    public Runnable getRunFunction() {
        return runFunction;
    }

    /**
     * Sets the function that will be run when {@link #run} is called.
     * <br>
     * IMPORTANT: This will override the default function that is generated by {@link #generateRunFunction()} so use it only if you know what you are doing.
     * <br>
     * Note: This will also be overridden if a wait for event is set or a base function is set.
     * @param runFunction the new run function
     * @throws IllegalArgumentException if the argument is null
     */
    public void setRunFunction(Runnable runFunction) {
        if (runFunction == null)
            throw new IllegalArgumentException("The argument runFunction can not be null!");
        this.runFunction = runFunction;
    }

    /**
     * Gets the default function that will be run when {@link #run} is called if there are no active runnables.
     * @return {@link #baseFunction}
     */
    public Runnable getBaseFunction() {
        return baseFunction;
    }

    /**
     * Sets the default function that will be run when {@link #run} is called if there are no active runnables.
     * @param baseFunction the new base function
     * @param overrideEvent if true this will override the runnable set by {@link #waitForEvent(String, EventManager, Runnable)} if there is one
     */
    public void setBaseFunction(Runnable baseFunction, boolean overrideEvent) {
        this.baseFunction = baseFunction;
        if(!waitingForEvent || overrideEvent)
            runFunction = generateRunFunction();
    }

    /**
     * Calls {@link #setBaseFunction(Runnable, boolean)} with overrideEvent set to false.
     * @param baseFunction the new base function
     */
    public void setBaseFunction(Runnable baseFunction) {
        setBaseFunction(baseFunction, false);
    }

    /**
     * Gets if this groups is waiting for an event
     * @return {@link #waitingForEvent}
     */
    public boolean isWaitingForEvent() {
        return waitingForEvent;
    }

    /**
     * Gets the default function that will be run when {@link #run} is called.
     * This function will run all queued actions and then all active runnables or a default function if specified.
     * @return the default run function
     */
    private Runnable generateRunFunction(){
        if(baseFunction != null)
            return () -> {
                runQueuedActions();
                if(activeRunnables.isEmpty())
                    baseFunction.run();
                else
                    activeRunnables.forEach((k, v) -> {
                        try{
                            v.run();
                        } catch (Exception e) {
                            throw new RuntimeException("Error running '" + k + "' in group '" + getName() + "'", e);
                        }
                    });
            };
        return () -> {
            runQueuedActions();
            activeRunnables.forEach((k, v) -> {
                try{
                    v.run();
                } catch (Exception e) {
                    throw new RuntimeException("Error running '" + k + "' in group '" + getName() + "'", e);
                }
            });
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
        waitingForEvent = true;
        manager.singleTimeAttachToEvent(event, "set run in group - " + getName(), () -> {
            runFunction = generateRunFunction();
            waitingForEvent = false;
        });
    }

    /**
     * Runs the {@link #runFunction}
     */
    @Override
    public void run(){
        runFunction.run();
    }
}
