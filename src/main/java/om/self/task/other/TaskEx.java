package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.Task;

import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * 1
 */
public class TaskEx extends Task {
    private Supplier<Boolean> end;
    private LinkedList<Runnable> steps = new LinkedList<>();
    private LinkedList<Supplier<Boolean>> ends = new LinkedList<>();

    private int task = 0;
    private boolean done = false;

    private boolean autoStart = true;


    //----------CONSTRUCTORS----------//
    /**
     * Constructor that just sets the name of this task.
     * @param name the name of this task
     */
    public TaskEx(String name){
        super(name);
    }

    /**
     * Constructor that sets the name of this task and attaches it to a Group with the key being the same as the name
     * @param name the name of this task
     * @param parent the Group you want to attach this task to (parentKey will equal name)
     */
    public TaskEx(String name, Group parent){
        super(name, parent);
    }

    /**
     * Constructor that sets the name of this task and attaches it to a parent Group with the key parentKey
     * @param name the name of this task
     * @param parentKey the name used to reference this task in the parent group (not used if the parent parameter is null)
     * @param parent the Group you want to attach this task to (if null then it won't have a parent)
     */
    public TaskEx(String name, String parentKey, Group parent){
        super(name, parentKey, parent);
    }


    //----------GETTERS and SETTERS----------//
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isAutoStartEnabled() {
        return autoStart;
    }


    //----------ADD/REMOVE STEP----------//
    /**
     * 1
     * @param step 1
     * @param end 1
     */
    public void addStep(Runnable step, Supplier<Boolean> end){
        if(step==null || end ==null) return;

        steps.add(step);
        ends.add(end);
        autoStart();
    }

    /**
     * 1
     * @param end 1
     */
    public void addStep(Supplier<Boolean> end){
        addStep(() -> {}, end);
    }

    /**
     * 1
     * @param step 1
     */
    public void addStep(Runnable step){
        addStep(step, () -> true);
    }

    /**
     * 1
     * @param step 1
     */
    public void addStep(Task step){
        addStep(step, step::isDone);
    }

    /**
     * 1
     * @param step 1
     * @param end 1
     * @param index 1
     */
    public void addStep(Runnable step, Supplier<Boolean> end, int index){
        if(step==null || end ==null) return;

        steps.add(index, step);
        ends.add(index, end);
        autoStart();
    }

    private void autoStart(){
        if(isAutoStartEnabled() && isParentAttached() && !isRunning())
            runCommand(Group.Command.START);
    }

    /**
     * removes and stops a step based on the index
     * @param index the index of the step you want to remove(starts at 0)
     * @param stopIfActive stops the step if it is currently running by incrementing to the next step
     */
    public void removeStep(int index, boolean stopIfActive){
        steps.remove(index);
        ends.remove(index);
        if(stopIfActive && task == index){
            setCurrStep(index);
        }
    }


    //----------ACTIONS----------//
    /**
     * 1
     */
    public void clear(){
        steps.clear();
        ends.clear();
        reset();
    }

    /**
     * 1
     */
    public void reset(){
        task = 0;
        done = false;
    }

    /**
     * 1
     */
    public void restart(){
        reset();
    }

    //----------RUN----------//
    /**
     * 1
     */
    @Override
    public void run(){
        getRunnable().run();
        if(end.get())
            setToNextStep();
    }


    //----------CHECKS----------//
    /**
     * 1
     * @param curr 1
     * @return 1
     */
    boolean checkIfDone(int curr){
        return curr >= steps.size();
    }

    /**
     * 1
     * @return 1
     */
    boolean checkIfDone(){
        done = checkIfDone(task);
        return done;
    }

    /**
     * 1
     * @return 1
     */
    @Override
    public boolean isDone(){
        return done;
    }


    //----------MANAGE STEPS----------//

    /**
     * 1
     * @return 1
     */
    public int getCurrentStep(){
        return task;
    }

    /**
     * 1
     * @param curr 1
     */
    public void setCurrStep(int curr){
        if (checkIfDone(curr)) {
            done = true;
            if(isParentAttached()) runCommand(Group.Command.QUE_PAUSE);
            return;
        }
        super.setRunnable(steps.get(curr));
        end = ends.get(curr);
        task = curr;
    }

    /**
     * 1
     */
    public void setToNextStep(){
        setCurrStep(task + 1);
    }

    /**
     * 1
     * @param tab 1
     * @param startTabs 1
     * @return 1
     */
    @Override
    public String getStatusString(String tab, int startTabs){
        String start = "";
        for(int i = 0; i < startTabs; i++){
            start += tab;
        }

        return super.getStatusString(tab, startTabs) + "\n" +
                start + tab + "Current Step: " + getCurrentStep() + "\n" +
                start + tab + "Done: " + isDone();
    }

    /**
     * 1
     * @param runnable the runnable action you want to run
     */
    @Override
    public void setRunnable(Runnable runnable) {
        throw new UnsupportedOperationException("you can't directly set the runnable with a taskEx!");
    }
}
