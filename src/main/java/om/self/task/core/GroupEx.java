package om.self.task.core;

import om.self.task.event.EventManager;

public class GroupEx extends Group{
    private Runnable runFunction = getBaseRunFunction();

    public GroupEx(String name) {
        super(name);
    }

    public GroupEx(String name, Group parent) {
        super(name, parent);
    }

    public GroupEx(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    private Runnable getBaseRunFunction(){
        return () -> {
            while(!queuedGroupActions.isEmpty()) queuedGroupActions.removeFirst().run();
            activeRunnables.forEach((k, v) -> v.run());
        };
    }

    public void waitForEvent(String event, EventManager manager, Runnable runnable){
        runFunction = runnable;
        manager.singleTimeAttachToEvent(event, "set run in group - " + getName(), () -> runFunction = getBaseRunFunction());
    }

    @Override
    public void run(){
        runFunction.run();
    }
}
