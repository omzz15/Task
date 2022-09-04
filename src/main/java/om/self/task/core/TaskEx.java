package om.self.task.core;

import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * 1
 */
public class TaskEx extends Task {
    private Supplier<Boolean> end;
    private LinkedList<Runnable> steps = new LinkedList<>();
    private LinkedList<Supplier<Boolean>> ends = new LinkedList<>();

    private int currentStep = 0;
    private boolean done = false;

    private boolean autoReset = true;
    private boolean queuedReset = false;


    //----------CONSTRUCTORS----------//
    /**
     * Constructor that just sets the name of this currentStep.
     *
     * @param name the name of this currentStep
     */
    public TaskEx(String name) {
        super(name);
    }

    /**
     * Constructor that sets the name of this currentStep and attaches it to a Group with the key being the same as the name
     *
     * @param name   the name of this currentStep
     * @param parent the Group you want to attach this currentStep to (parentKey will equal name)
     */
    public TaskEx(String name, Group parent) {
        super(name, parent);
    }

    /**
     * Constructor that sets the name of this currentStep and attaches it to a parent Group with the key parentKey
     *
     * @param name      the name of this currentStep
     * @param parentKey the name used to reference this currentStep in the parent group (not used if the parent parameter is null)
     * @param parent    the Group you want to attach this currentStep to (if null then it won't have a parent)
     */
    public TaskEx(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    @Override
    protected void construct(String name, String parentKey, Group parent) {
        setAutoPause(true);
        super.construct(name, parentKey, parent);
    }

    //----------GETTERS and SETTERS----------//
    /**
     * 1
     *
     * @return 1
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * 1
     *
     * @param curr 1
     */
    public void setCurrentStep(int curr) {
        //auto pause/reset
        if (curr >= steps.size()) {
            done = true;
            if(isAutoPauseEnabled() && isParentAttached()) runCommand(Group.Command.QUE_PAUSE);
            if(isAutoResetEnabled() && curr != 0) queReset();
            return;
        }
        super.setRunnable(steps.get(curr));
        end = ends.get(curr);
        currentStep = curr;
    }

    /**
     * 1
     *
     * @return 1
     */
    @Override
    public boolean isDone() {
        return done;
    }

    /**
     *
     * @param autoReset
     */
    public void setAutoReset(boolean autoReset) {
        this.autoReset = autoReset;
    }

    /**
     *
     * @return
     */
    public boolean isAutoResetEnabled() {
        return autoReset;
    }

    //----------ADD/REMOVE STEP----------//
    /**
     * 1
     *
     * @param step 1
     * @param end  1
     */
    public void addStep(Runnable step, Supplier<Boolean> end) {
        if (step == null || end == null) return;

        steps.add(step);
        ends.add(end);

        if(steps.size() == 1) {
            onEmptyAddStep();
        }
    }

    /**
     * 1
     *
     * @param end 1
     */
    public void addStep(Supplier<Boolean> end) {
        addStep(() -> {
        }, end);
    }

    /**
     * 1
     *
     * @param step 1
     */
    public void addStep(Runnable step) {
        addStep(step, () -> true);
    }

    /**
     * 1
     *
     * @param step 1
     */
    public void addStep(Task step) {
        addStep(step, step::isDone);
    }

    /**
     * 1
     *
     * @param step  1
     * @param end   1
     * @param index 1
     */
    public void addStep(Runnable step, Supplier<Boolean> end, int index) {
        if (step == null || end == null) return;

        steps.add(index, step);
        ends.add(index, end);

        if(steps.size() == 1) {
            onEmptyAddStep();
        }
    }

    /**
     * This will be called when {@link TaskEx#steps} is empty, and it is responsible for ensuring that {@link Task#runnable} and {@link TaskEx#end} are initially set, so they don't throw errors when {@link TaskEx#run()} is called. It is also responsible for running auto start
     */
    private void onEmptyAddStep() {
        setCurrentStep(0);
        //if (isAutoStartEnabled() && isParentAttached() && !isRunning())
        //    runCommand(Group.Command.START);
    }

    /**
     * removes and stops a step based on the index
     *
     * @param index        the index of the step you want to remove(starts at 0)
     * @param stopIfActive stops the step if it is currently running by incrementing to the next step
     */
    public void removeStep(int index, boolean stopIfActive) {
        steps.remove(index);
        ends.remove(index);
        if(index < currentStep) {
            currentStep --;
            return;
        }
        if (currentStep == index && stopIfActive) {
            setCurrentStep(index);
        }
    }


    //----------MANAGE STEPS----------//
    /**
     * 1
     */
    public void setToNextStep() {
        setCurrentStep(currentStep + 1);
    }


    //----------ACTIONS----------//
    /**
     * 1
     */
    public void clear() {
        steps.clear();
        ends.clear();
        super.setRunnable(null);
        end = null;
        reset();
    }

    /**
     * 1
     */
    public void reset() {
        done = false;
        setCurrentStep(0);
    }

    public void queReset(){
        queuedReset = true;
    }

    /**
     * 1
     */
    public void restart() {
        reset();
        runCommand(Group.Command.START);
    }


    //----------OVERRIDE Task METHODS----------//
    /**
     * 1
     */
    @Override
    public void run() {
        if(queuedReset){
            reset();
            queuedReset = false;
        }

        getRunnable().run();
        if (end.get())
            setToNextStep();
    }

    /**
     * 1
     *
     * @param runnable the runnable action you want to run
     */
    @Override
    public void setRunnable(Runnable runnable) {
        throw new UnsupportedOperationException("you can't directly set the runnable with a taskEx!");
    }



    /**
     * 1
     *
     * @param tab       1
     * @param startTabs 1
     * @return 1
     */
    @Override
    public String getInfo(String tab, int startTabs, boolean extend) {
        String start = tab.repeat(startTabs);
        StringBuilder str = getBaseInfo(tab, start);

        str.append("\n");
        str.append(start + tab + "Completed: " + done);

        str.append("\n");
        str.append(start + tab + "Current Step: " + (currentStep + 1));

        if(extend) {
            str.append("\n");
            str.append(start + tab + "Total Steps: " + steps.size());
            str.append("\n");
            str.append(start + tab + "Auto Start: " + isAutoStartEnabled());
            str.append("\n");
            str.append(start + tab + "Auto Pause: " + isAutoPauseEnabled());
            str.append("\n");
            str.append(start + tab + "Auto Reset: " + isAutoPauseEnabled());
        }
        return str.toString();
    }
}
