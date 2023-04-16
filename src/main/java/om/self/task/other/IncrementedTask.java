package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

import java.util.function.Supplier;

/**
 * 1
 */
public class IncrementedTask extends TaskEx {
    private int i;

    public IncrementedTask(String name) {
        super(name);
    }

    /**
     * 1
     * @param name 1
     * @param parent 1
     */
    public IncrementedTask(String name, Group parent) {
        super(name, parent);
    }

    /**
     * 1
     * @param name 1
     * @param parentKey 1
     * @param parent 1
     */
    public IncrementedTask(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * 1
     * @param step 1
     * @param start 1
     * @param increment 1
     * @param end 1
     */
    public void addIncrementedStep(Runnable step, int start, int increment, Supplier<Boolean> end){
        if(step == null || end == null || increment == 0) return;

        addStep(() -> {
            i = start;
        });

        addStep(() -> {
            step.run();
            i += increment;
        }, end);
    }

    /**
     * 1
     * @param step 1
     * @param start 1
     * @param increment 1
     * @param end 1
     */
    public void addIncrementedStep(Runnable step, int start, int increment, int end){
        addIncrementedStep(step, start, increment, () -> (i >= end));
    }

    /**
     * 1
     * @param step 1
     * @param times 1
     */
    public void addIncrementedStep(Runnable step, int times){
        addIncrementedStep(step, 0, 1, times);
    }

    /**
     * 1
     * @return 1
     */
    public int getI(){
       return i; 
    }
}
