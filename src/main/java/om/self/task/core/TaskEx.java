package om.self.task.core;

import om.self.task.event.EventContainer;
import om.self.task.event.EventManager;

import java.util.LinkedList;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * 1
 */
public class TaskEx extends Task {
    private Supplier<Boolean> end;
    private LinkedList<Runnable> steps = new LinkedList<>();
    private LinkedList<Supplier<Boolean>> ends = new LinkedList<>();

    private int currentStep = 0;
    private boolean done = false;

    /**
     * 1
     */
    public boolean autoReset = false;

    private boolean waiting = false;


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
        autoPause = true;
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
            if(autoPause && isParentAttached()) runCommand(Group.Command.PAUSE);
            if(autoReset && curr != 0) reset();
            return;
        }
        super.setRunnable(steps.get(curr));
        end = ends.get(curr);
        currentStep = curr;

        if(waiting)
            runCommand(Group.Command.PAUSE);
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
     * runs the runnable once then move on to the next step. Equivalent of addStep(Runnable, () -> true)
     * @param step the thing you want run once
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

    public void addStep(Group step) {
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

    public void addNextStep(Runnable step, Supplier<Boolean> end){
        addStep(step, end, getCurrentStep() + 1);
    }

    public void addConcurrentSteps(Runnable... steps){
        Group g = new Group("concurrent steps");
        for(int i = 0; i < steps.length; i++){
            g.attachChild("concurrent " + i, steps[i]);
        }
        addStep(g);
    }

    /**
     * This will wait for any of the events to complete then remove everything
     * @param event the event that should trigger this task
     */
    public void waitForEvent(EventContainer event){
        addStep(() -> {
            getParent().setWaiting(true);
            waiting = true;
            event.manager.singleTimeAttachToEvent(event.event, "start task - " + getName(), () -> {
                getParent().setWaiting(false);
                waiting = false;
                runCommand(Group.Command.START);
                System.out.println("event triggered");
            });
            runCommand(Group.Command.PAUSE);
        });
    }

    /**
     * This will wait for any of the events to complete then remove everything and continue
     * @param events the events that should trigger this task
     */
    public void waitForEvents(EventContainer... events){
        addStep(() -> {
            getParent().setWaiting(true);
            for(EventContainer event : events)
                event.manager.attachToEvent(event.event, "start task - " + getName(), () -> {
                    getParent().setWaiting(false);
                    runCommand(Group.Command.START);
                });
            runCommand(Group.Command.PAUSE);
        });

        addStep(() -> {
            for(EventContainer event : events)
                event.manager.detachFromEvent(event.event, "start task - " + getName());
        });
    }

    public void waitForEvent(String event, EventManager manager, Runnable runnable){
        addStep(() -> manager.singleTimeAttachToEvent(event, "next step on task - " + getName(), this::setToNextStep));
        addStep(runnable, () -> false);
    }

    /**
     * This will be called when {@link TaskEx#steps} is empty, and it is responsible for ensuring that {@link Task#runnable} and {@link TaskEx#end} are initially set, so they don't throw errors when {@link TaskEx#run()} is called.
     */
    private void onEmptyAddStep() {
        setCurrentStep(0);
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
        String start = repeat(tab, startTabs);
        StringBuilder str = getBaseInfo(tab, start);

        str.append("\n");
        str.append(start + tab + "Completed: " + done);

        str.append("\n");
        str.append(start + tab + "Current Step: " + (currentStep + 1));

        if(extend) {
            str.append("\n");
            str.append(start + tab + "Total Steps: " + steps.size());
            str.append("\n");
            str.append(start + tab + "Auto Start: " + autoStart);
            str.append("\n");
            str.append(start + tab + "Auto Pause: " + autoPause);
            str.append("\n");
            str.append(start + tab + "Auto Reset: " + autoReset);
        }
        return str.toString();
    }
}
