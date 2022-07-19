package om.self.task.singlethreaded;

import om.self.task.singlethreaded.core.TaskEx;
import om.self.task.core.TaskRunner;

public class IncrementedTask extends TaskEx {
    private int i;

    public IncrementedTask(String name, TaskRunner taskRunner) {
        super(name, taskRunner);
    }

    public IncrementedTask(String name) {
        super(name);
    }
    
    public void addIncrementedStep(Step step, int start, int increment, EndPoint end){
        if(step == null || end == null || increment == 0) return;

        addStep(() -> {
            i = start;
        });

        addStep(() -> {
            step.apply();
            i += increment;
        }, end);
    }

    public void addIncrementedStep(Step step, int start, int incriemnt, int end){
        addIncrementedStep(step, start, incriemnt, () -> (i >= end));
    }

    public void addIncrementedStep(Step step, int times){
        addIncrementedStep(step, 0, 1, times);
    }

    public int getI(){
       return i; 
    }
}
