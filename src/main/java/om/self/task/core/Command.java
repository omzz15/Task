package om.self.task.core;

import java.util.AbstractMap;
import java.util.Map;

//////////////
//UNTESTED!!//
//////////////

/**
 * Heavy inspiration from FRC CommandBase
 */
public abstract class Command extends Task{
    /**
     * Constructor that sets the name of this task and attaches it to a parent Group with the key parentKey
     *
     * @param name      the name of this task
     * @param parentKey the name used to reference this task in the parent group (not used if the parent parameter is null)
     * @param parent    the Group you want to attach this task to (if null then it won't have a parent)
     */
    public Command(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * Constructor that sets the name of this task and attaches it to a Group with the key being the same as the name
     *
     * @param name   the name of this task
     * @param parent the Group you want to attach this task to (parentKey will equal name)
     */
    public Command(String name, Group parent) {
        super(name, parent);
    }

    /**
     * Constructor that just sets the name of this task.
     *
     * @param name the name of this task
     */
    public Command(String name) {
        super(name);
    }

    public abstract void start();

    public abstract void stop(boolean isInterrupted);

    /**
     * runs the action
     */
    @Override
    public void run() {
        onRun();
        if(isFinished()) {
            runCommand(Group.Command.PAUSE, new AbstractMap.SimpleEntry<>(Group.CommandVars.interrupted, false));
        }
    }

    public abstract void onRun();

    public abstract boolean isFinished();
}
