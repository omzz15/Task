package om.self.task.core;

import om.self.task.event.EventContainer;
import om.self.task.event.EventManager;

import java.util.LinkedList;
import java.util.function.Supplier;

import static om.self.task.other.Utils.repeat;

/**
 * An extension of {@link Task} that allows for multiple steps to be added to it.
 */
public class TaskEx extends Task {
    /**
     * This will check if the current step is complete
     */
    private Supplier<Boolean> end;
    /**
     * A list of steps to run in this task
     */
    private LinkedList<Runnable> steps = new LinkedList<>();
    /**
     * A list of checks to run to see if the current step is complete. Each check corresponds to the step at the same index in {@link #steps}.
     */
    private LinkedList<Supplier<Boolean>> ends = new LinkedList<>();

    /**
     * The current step this task is on
     */
    private int currentStep = 0;
    /**
     * If the task has been completed (gets reset when the task is reset)
     */
    private boolean done = false;

    /**
     * Weather the task should automatically reset once it is done
     */
    public boolean autoReset = false;

    /**
     * Flag to indicate if the task is waiting for an event
     */
    private boolean waiting = false;


    //----------CONSTRUCTORS----------//
    /**
     * Constructor that just sets the name of this task.
     *
     * @param name the name of this task
     */
    public TaskEx(String name) {
        this(name, null, null);
    }

    /**
     * Constructor that sets the name of this task and attaches it to a Group with the key being the same as the name
     *
     * @param name   the name of this task
     * @param parent the Group you want to attach this task to (parentKey will equal name)
     */
    public TaskEx(String name, Group parent) {
        this(name, name, parent);
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
        autoPause = true; //set the new default behavior for TaskEx
    }

    //----------GETTERS and SETTERS----------//
    /**
     * Gets the step this task is currently on
     * @return {@link #currentStep}
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * set the current step this task is on
     * (if it is greater than the number of steps then it will set {@link #done} to true and pause the task)
     * @param curr the step you want to set this task to (0 based index)
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
     * Whether this is done
     * @return {@link #done}
     */
    @Override
    public boolean isDone() {
        return done;
    }

    //----------ADD/REMOVE STEP----------//
    /**
     * Adds a step to this task at the end of the list
     * @param step the step you want to run
     * @param end  the check to see if the step is done
     *
     * @throws IllegalArgumentException if step or end is null
     */
    public void addStep(Runnable step, Supplier<Boolean> end) {
        if (step == null || end == null)
            throw new IllegalArgumentException("step and end cannot be null");

        steps.add(step);
        ends.add(end);

        if(steps.size() == 1) {
            onEmptyAddStep();
        }
    }

    /**
     * Adds a step to this task that just waits for the end condition to be true. Equivalent of addStep(() -> {}, end)
     * @param end the check to see if the step is done
     * @see #addStep(Runnable, Supplier)
     */
    public void addStep(Supplier<Boolean> end) {
        addStep(() -> {
        }, end);
    }

    /**
     * Adds a step that will run once then move on to the next step. Equivalent of addStep(step, () -> true)
     * @param step the thing you want run once
     * @see #addStep(Runnable, Supplier)
     */
    public void addStep(Runnable step) {
        addStep(step, () -> true);
    }

    /**
     * Adds a step that will run a {@link Task} until it is done
     * @param step the task you want to run
     */
    public void addStep(Task step) {
        addStep(step, step::isDone);
    }

    /**
     * Adds a step that will run a {@link Group} until it is done
     * @param step the group you want to run
     */
    public void addStep(Group step) {
        addStep(step, step::isDone);
    }

    /**
     * Adds a step to a specific index in the list
     * @param step  the step you want to run
     * @param end   the check to see if the step is done
     * @param index the index you want to add the step at (0 based index)
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
     * Like {@link #addStep(Runnable, Supplier, int)} but will add it to the step right after this one
     * @param step the step you want to run
     * @param end the check to see if the step is done
     */
    public void addNextStep(Runnable step, Supplier<Boolean> end){
        addStep(step, end, getCurrentStep() + 1);
    }

    /**
     * It will put all the steps into a group, so they can run concurrently
     * @param steps the steps you want to run concurrently
     */
    public void addConcurrentSteps(Runnable... steps){
        Group g = new Group("concurrent steps");
        for(int i = 0; i < steps.length; i++){
            g.attachChild("concurrent " + i, steps[i]);
        }
        addStep(g);
    }

    /**
     * This will pause the task and wait for an event to complete then resume as a step
     * @param event the event that should trigger this task
     */
    public void waitForEvent(EventContainer event){
        addStep(() -> {
            getParent().setWaiting(true);
            waiting = true;
            event.attach("start task - " + getName(), () -> {
                getParent().setWaiting(false);
                waiting = false;
                runCommand(Group.Command.START);
                System.out.println("event triggered"); //TODO remove for production 
            });
            runCommand(Group.Command.PAUSE);
        });
    }

    /**
     * This will pause the task and wait for any of the events to complete then remove everything and continue
     * @param events the events that should trigger this task
     */
    public void waitForEvents(EventContainer... events){
        addStep(() -> {
            getParent().setWaiting(true);
            for(EventContainer event : events)
                event.attach("start task - " + getName(), () -> {
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

    /**
     * This will keep running the runnable until the event is triggered
     * @param event the event that should trigger this task
     * @param runnable the runnable that should be run until the event is triggered
     */
    public void waitForEvent(EventContainer event, Runnable runnable){
        addStep(() -> event.singleTimeAttach("next step on task - " + getName(), this::setToNextStep));
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
     * Just sets the current step to the next one
     */
    public void setToNextStep() {
        setCurrentStep(currentStep + 1);
    }


    //----------ACTIONS----------//
    /**
     * clears everything from this task and resets it
     */
    public void clear() {
        steps.clear();
        ends.clear();
        super.setRunnable(null);
        end = null;
        reset();
    }

    /**
     * reset the task to the first step and set done to false
     */
    public void reset() {
        done = false;
        setCurrentStep(0);
    }

    /**
     * resets the task and starts it <br>
     * Note: This only works if a parent is attached
     */
    public void restart() {
        reset();
        runCommand(Group.Command.START);
    }


    //----------OVERRIDE Task METHODS----------//
    /**
     * Runs the current step and checks if it should go to the next one. <br>
     * Note: The parent calls this, so you don't need to unless there is no parent.
     */
    @Override
    public void run() {
        getRunnable().run();
        if (end.get())
            setToNextStep();
    }

    /**
     * Throws an error because you can't directly set the runnable with a TaskEx
     * @param runnable the runnable action you want to run
     * @throws UnsupportedOperationException because you can't directly set the runnable with a TaskEx
     */
    @Override
    public void setRunnable(Runnable runnable) {
        throw new UnsupportedOperationException("you can't directly set the runnable with a taskEx!");
    }



    /**
     * gets the info for this TaskEx (if it is complete and the current step)
     * @param tab       the style of the tabs
     * @param startTabs the number of tabs to start with
     * @param extend    if it should add extra info (total steps, auto start, auto pause, and auto reset)
     * @return the info for this TaskEx
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
