package task;

public class IncrimentedTask extends TaskEx{
    private int i;

    public IncrimentedTask(String name, TaskRunner taskRunner) {
        super(name, taskRunner);
    }

    public IncrimentedTask(String name) {
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

    public void addIncrimentedStep(Step step, int start, int incriemnt, int end){
        addIncrementedStep(step, start, incriemnt, () -> (i >= end));
    }

    public void addIncrimentedStep(Step step, int times){
        addIncrimentedStep(step, 0, 1, times);
    }

    public int getI(){
       return i; 
    }
}
