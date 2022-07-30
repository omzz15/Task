package om.self.task.other;

import om.self.task.core.Group;

import java.util.function.Supplier;

public class IncrementedTask extends TaskEx{
    private int i;

    public IncrementedTask(String name) {
        super(name);
    }

    public IncrementedTask(String name, Group parent) {
        super(name, parent);
    }

    public IncrementedTask(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

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

    public void addIncrementedStep(Runnable step, int start, int increment, int end){
        addIncrementedStep(step, start, increment, () -> (i >= end));
    }

    public void addIncrementedStep(Runnable step, int times){
        addIncrementedStep(step, 0, 1, times);
    }

    public int getI(){
       return i; 
    }
}
