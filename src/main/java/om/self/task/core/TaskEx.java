package om.self.task.core;

import java.util.LinkedList;
import java.util.function.Supplier;

public class TaskEx extends Task{
    private Supplier<Boolean> end;
    private LinkedList<Runnable> steps = new LinkedList<>();
    private LinkedList<Supplier<Boolean>> ends = new LinkedList<>();

    private int task = 0;
    private boolean done = false;

    public TaskEx(String name){
        super(name);
    }

    public TaskEx(String name, Group parent){
        super(name, parent);
    }

    public void addStep(Runnable step, Supplier<Boolean> end){
        steps.add(step);
        ends.add(end);
    }

    public void addStep(Supplier<Boolean> end){
        steps.add(() -> {});
        ends.add(end);
    }

    public void addStep(Runnable step){
        steps.add(step);
        ends.add(() -> true);
    }

    public void addStep(Runnable step, Supplier<Boolean> end, int index){
        steps.add(index, step);
        ends.add(index, end);
    }


    /**
     * removes and stops a step based on the index
     * @param index the index of the step you want to remove(starts at 0)
     * @param stopIfActive stops the step if it is currently running
     */
    public void removeStep(int index, boolean stopIfActive){
        steps.remove(index);
        ends.remove(index);
        if(stopIfActive && task == index){
            setToNextStep();
        }
    }

    public void clear(){
        steps.clear();
        ends.clear();
        reset();
    }

    public void reset(){
        task = 0;
        done = false;
    }

    public void restart(){
        reset();
        start();
    }

    @Override
    public void start(){
        if(checkIfDone()) return;

        super.start();
        setCurrStep(task);
    }

    @Override
    public void run(){
        getRunnable().run();
        if(end.get())
            setToNextStep();
    }

    boolean checkIfDone(int curr){
        return curr >= steps.size();
    }

    boolean checkIfDone(){
        done = checkIfDone(task);
        return done;
    }

    @Override
    public boolean isDone(){
        return done;
    }

    public int getCurrentStep(){
        return task;
    }

    public void setCurrStep(int curr){
        if (checkIfDone(curr)) {
            done = true;
            if(isParentAttached()) quePause();
            return;
        }
        setRunnable(steps.get(curr));
        end = ends.get(curr);
        task = curr;
    }

    public void setToNextStep(){
        setCurrStep(task + 1);
    }

    @Override
    public String getStatusString(String tab, int startTabs){
        String start = "";
        for(int i = 0; i < startTabs; i++){
            start += tab;
        }

        return super.getStatusString(tab, startTabs) + "\n" +
                start + tab + "Current Step: " + task + "\n" +
                start + tab + "Done: " + done;
    }
}
