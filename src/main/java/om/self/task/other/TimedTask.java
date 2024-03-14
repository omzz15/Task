package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

import java.util.function.Supplier;

/**
 * An extension of {@link TaskEx} that allows steps that are run for a certain amount of time
 */
public class TimedTask extends TaskEx {
    /**
     * The start time of the current timed step
     */
    private long startTime;

    /**
     * Constructor that sets the name of this task
     * @param name the name of this task
     */
    public TimedTask(String name) {
        super(name);
    }

    /**
     * Constructor that sets the name and parent of this task
     * @param name the name of this task
     * @param parent the parent of this task
     */
    public TimedTask(String name, Group parent){
		super(name, parent);
	}

    /**
     * Constructor that sets the name, parent key, and parent of this task
     * @param name the name of this task
     * @param parentKey the parent key associated with the parent of this task
     * @param parent the parent of this task
     */
    public TimedTask(String name, String parentKey, Group parent){
        super(name, parentKey, parent);
    }

    /**
     * Adds a step that runs for a certain amount of time
     * @param step the step to run
     * @param time the amount of time to run the step for
     */
    public void addTimedStep(Runnable step, int time){
        //sanitize input
        if(time <= 0 || step == null) return;

        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(step, () -> (System.currentTimeMillis() - startTime >= time));
    }

    /**
     * Adds a step that runs for a certain amount of time or until a condition is met
     * @param step the step to run
     * @param end the condition that determines if the step should stop running even if the time isn't up
     * @param time the amount of time to run the step for
     */
    public void addTimedStep(Runnable step, Supplier<Boolean> end, int time){
        //sanitize input
        if(time <= 0 || step == null) return;

        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(step, () -> (end.get() || (System.currentTimeMillis() - startTime >= time)));
    }

    /**
     * Just {@link #addTimedStep(Runnable, int)} but with no Runnable, so it just waits for the time to pass
     * @param delay the amount of time to wait in milliseconds
     */
    public void addDelay(int delay){
        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(() -> (System.currentTimeMillis() - startTime >= delay));
    }

    /**
     * Just {@link #addTimedStep(Runnable, Supplier, int)} but with no Runnable, so it just waits for the time to pass
     * @param delay the amount of time to wait
     * @param end condition that determines if the step should stop running even if the time isn't up
     */
    public void addConditionalDelay(int delay, Supplier<Boolean> end) {
        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(() -> (System.currentTimeMillis() - startTime >= delay || end.get()));
    }

    /**
     * Gets the amount of time that has passed since the start of the last timed step. Useful for knowing time within a timed step.
     * @return the amount of time that has passed since the start of the current timed step
     */
    public int getCurrentRunTime(){
        return (int)(System.currentTimeMillis() - startTime);
    }
}
