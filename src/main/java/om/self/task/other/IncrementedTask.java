package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An extension of {@link TaskEx} that allows steps that are like an incrementing for loop
 */
public class IncrementedTask extends TaskEx {
    /**
     * The current value of the incrementing for loop
     */
    private int i;

    /**
     * Constructor that sets the name of this task
     * @param name the name of this task
     */
    public IncrementedTask(String name) {
        super(name);
    }

    /**
     * Constructor that sets the name and parent of this task
     * @param name the name of this task
     * @param parent the parent of this task
     */
    public IncrementedTask(String name, Group parent) {
        super(name, parent);
    }

    /**
     * Constructor that sets the name, parent key, and parent of this task
     * @param name the name of this task
     * @param parentKey the parent key associated with the parent of this task
     * @param parent the parent of this task
     */
    public IncrementedTask(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * A general incrementing step that can have a custom start, increment, and end condition
     * @param step the step to run while incrementing
     * @param start the starting value of the incrementing loop
     * @param increment the amount to increment by each time
     * @param end the condition that determines when to stop incrementing
     */
    public void addIncrementedStep(Runnable step, int start, int increment, Supplier<Boolean> end){
        if(step == null || end == null || increment == 0) return;

        addStep(() -> i = start);

        addStep(() -> {
            step.run();
            i += increment;
        }, end);
    }

    /**
     * A version of {@link #addIncrementedStep(Runnable, int, int, Supplier)} that uses a consumer as the step instead of a Runnable.
     * @param step the step to run while incrementing
     * @param start the starting value of the incrementing loop
     * @param increment the amount to increment by each time
     * @param end the condition that determines when to stop incrementing
     */
    public void addIncrementedStep(Consumer<Integer> step, int start, int increment, Supplier<Boolean> end){
        if(step == null || end == null || increment == 0) return;

        addStep(() -> i = start);

        addStep(() -> {
            step.accept(i);
            i += increment;
        }, end);
    }

    /**
     * A version of {@link #addIncrementedStep(Runnable, int, int, Supplier)} that just checks
     * if the incrementing value is greater than or equal to the end. <br>
     * Note: This can not have a negative increment, use {@link #addIncrementedStep(Runnable, int, int, Supplier)} instead
     * @param step the step to run while incrementing
     * @param start the starting value of the incrementing loop
     * @param increment the amount to increment by each time (must be positive)
     * @param end the value to stop incrementing at
     */
    public void addIncrementedStep(Runnable step, int start, int increment, int end){
        addIncrementedStep(step, start, increment, () -> (i >= end));
    }

    /**
     * A specific version of {@link #addIncrementedStep(Runnable, int, int, Supplier)} that just starts at 0 and increments by one for the specified number of times.
     * @param step the step to run while incrementing
     * @param times the number of times to increment and run the step
     */
    public void addIncrementedStep(Runnable step, int times){
        addIncrementedStep(step, 0, 1, times);
    }

    /**
     * returns the current value of the incrementing for loop
     * @return the current value of the incrementing for loop
     */
    public int getI(){
       return i; 
    }
}
