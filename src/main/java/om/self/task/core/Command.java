package om.self.task.core;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Heavily inspired by FRC CommandBase, this class adds some features like start and stop methods and an interrupted boolean
 */
public abstract class Command extends Task{
    /**
     * Constructor that sets the name of this task and attaches it to a parent Group with the key parentKey
     *
     * @param name      the name of this task
     * @param parentKey the name used to reference this task in the parent group (not used if the parent parameter is null)
     * @param parent    the Group you want to attach this task to (if null, then it won't have a parent)
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

    /**
     * This will get called once when the command is started
     */
    public abstract void start();

    /**
     * This will get called once when the command is stopped
     * @param isInterrupted whether the command was interrupted or it stopped because it finished (when {@link #isFinished()} is true)
     */
    public abstract void stop(boolean isInterrupted);

    /**
     * runs the action <br>
     * IMPORTANT: the user SHOULD NOT call this,
     * it is called by the group and is started with {@link #runCommand(Group.Command, Map.Entry[])}
     */
    @Override
    public void run() {
        onRun();
        if(isFinished()) {
            runCommand(Group.Command.PAUSE, new AbstractMap.SimpleEntry<>(Group.CommandVars.interrupted, false));
        }
    }

    /**
     * This will get called every time the command is run
     */
    public abstract void onRun();

    /**
     * This will get called every time after the command is run to check if it is done
     * @return whether the command is done
     */
    public abstract boolean isFinished();
}
