package om.self.task.core;

import java.util.LinkedList;

public abstract class Task {
    //logging tasks
    private static LinkedList<Task> allTasks = new LinkedList<>();
    private static boolean logTasks = false;

    //task info
    private String name;
    private TaskRunner taskRunner;
    /**
     * the name used to identify this task in a TaskRunner if it is attached
     */
    private String containerName;
    /**
     * what this task actually executes
     */
    private Runnable step;

    /**
     * Construtor that sets the name of this task, attaches it to taskRunner with the containerName paramater, and exicutes action once it is attached
     * @param name the name of this task
     * @param taskRunner the TaskRunner you want to attach this task to
     * @param containerName the name used to refrence this task in the TaskRunner
     * @param action the action to run once this task has been attached to the TaskRunner
     */
    public Task(String name, TaskRunner taskRunner, String containerName, TaskRunner.Action action){
        construct(name, taskRunner, containerName, action);
    }

    /**
     * Constructor that sets the name of this task and attaches it to taskRunner with the name paramater
     * @param name the name of this task
     * @param taskRunner the TaskRunner you want to attach this task to (containerName will equal name)
     */
    public Task(String name, TaskRunner taskRunner){
        construct(name, taskRunner, name, TaskRunner.Action.NONE);
    }

    /**
     * Constructor that just sets the name of this task. To run wihtout TaskRunner use the run() method
     * @param name the name of this task
     */
    public Task(String name){
        construct(name, null, null, null);
    }

    /**
     * the method called by constructors to initilize variables and optionally set taskRunner
     * @param name the name of this task
     * @param taskRunner the TaskRunner you want to attach this task to (if null then it wont attach to a taskRunner)
     * @param containerName the name used to refrence this task in the TaskRunner (not used if taskRunner is null)
     * @param action the action to run once this task has been attached to the TaskRunner (not used if taskRunner is null)
     */
    private void construct(String name, TaskRunner taskRunner, String containerName, TaskRunner.Action action){
        setName(name);
        if(taskRunner != null)
            taskRunner.addTask(containerName, this, action);
        if(logTasks) allTasks.add(this);
    }

    /**
     * sets the name of this task - setter for variable name
     * @param name the name you want to set this task to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the name of this task - getter for variable name
     */
    public String getName() {
        return name;
    }

    /**
     * attaches this task to a TaskRunner with a specific name
     * @param taskRunner the TaskRunner you want to attach this task to
     * @param containerName the name used to refrence this task in the TaskRunner
     */
    public void attachTaskRunner(TaskRunner taskRunner, String containerName){
        this.taskRunner = taskRunner;
        this.containerName = containerName;
        taskRunner.addTaskRaw(containerName, this);
    }

    /**
     * attaches this task to taskRunner using this task's name
     * @param taskRunner the task runner to attach this task to
     */
    public void attachTaskRunner(TaskRunner taskRunner){
        attachTaskRunner(taskRunner, getName());
    }

    /**
     * removes the attached taskRunner from this task and deletes all refrences to this task in taskRunner
     * @throws NullPointerException if taskRunner has not been attached
     */
    public void removeTaskRunner(){
        taskRunner.runAction(containerName, TaskRunner.Action.REMOVE_FROM_QUE);
        taskRunner.runAction(containerName, TaskRunner.Action.PAUSE);
        taskRunner.removeTaskRaw(containerName);
        this.taskRunner = null;
        this.containerName = null;
    }

    /**
     * get the current attached taskRunner (returns null if no taskRunner is attached) - getter method for variable taskRunner
     * @return the variable taskRunner
     */
    public TaskRunner getTaskRunner(){
        return taskRunner;
    }

    /**
     * checks if a taskRunner is currently attached to this task
     * @return if a taksRunner is attached
     */
    public boolean isTaskRunnerAttached(){
        return taskRunner != null;
    }

    /**
     * sets the step (the function that is run by taskRunner or run()) - setter method for variable step
     * @param step the step you want to run
     */
    public void setStep(Runnable step){
        this.step = step;
    }

    /**
     * gets the step (the function that is run by taskRunner or run()) that is currently set - getter method for variable step
     * @return the currently set step
     */
    public Runnable getStep(){
        return step;
    }

    /**
     * gets the containerName ()
     * @return
     */
    public String getContainerName(){
        return containerName;
    }

    public void start(){
        if(isTaskRunnerAttached())
            taskRunner.addTaskToActiveTasks(containerName, this);
    }

    public void pause(){
        if(isTaskRunnerAttached())
            taskRunner.removeTaskFromActiveTasks(containerName);
    }

    public void quePause(){
        taskRunner.quePause(containerName);
    }

    public void reset(){}

    public void restart(){}

    /**
     * runs the task without checking if it is running (only use with TaskRunner)
     */
    void runRaw(){
        try{
            step.run();
        }catch(NullPointerException e){
            System.out.println("Warning null pointer exception thrown by " + this + ". Make sure to set the step!");
        }
    }

    /**
     * runs the task if it is running
     */
    public void run(){
        runRaw();
    }

    public String getStatusString(String tab, int startTabs){
        String start = "";
        for(int i = 0; i < startTabs; i++){
            start += tab;
        }
        return start + name + " as " + getClass() + ":\n" +
                start + tab +  "Running: " + running;
    }


    public void printStatusString(){
        System.out.println(getStatusString("\t", 0));
    }

    public static void setTaskLogging(boolean value){
        logTasks = value;
    }

    public static LinkedList<Task> getAllTasks(){
        //if(!logTasks)
        //Logger.addMessage("while calling Task.getAllTasks(): Task Logging was disabled so tasks may not be stored. \nTo enable task logging call Task.setTaskLogging(true) before making tasks.", Message.Type.WARNING);
        return allTasks;
    }

    public static Task getTaskWithName(String name){
        if(!logTasks)
            System.out.println("WARNING while calling Task.getTaskWithName(): Task Logging was disabled so tasks may not be stored. \nTo enable task logging call Task.setTaskLogging(true) before making tasks.");
        for(Task t : allTasks)
            if(t.getName().equals(name))
                return t;
        return null;
    }
}
