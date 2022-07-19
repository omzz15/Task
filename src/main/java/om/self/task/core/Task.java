package om.self.task.core;
import java.util.LinkedList;

import om.self.logger.Logger;
import om.self.logger.Message;
import om.self.task.core.TaskRunner.Action;

/**
 * A simple task that will exicute a Lambda Function with no input or output.
 * Ways to run are using the run() method or attaching to a TaskRunner.  
 */
public class Task implements Runnable{

	private static LinkedList<Task> allTasks = new LinkedList<>();

	private String name;
	private TaskRunner taskRunner;
	private String containerName;
	/**
	 * the Runnable thing that you want contained inside this task
	 * <p></p>
	 * NOTE: this may be run inside a different thread if you are using multithreaded components
	 */
	private Runnable action;


	/**
	 * Construtor that sets the name of this task, attaches it to taskRunner with the containerName paramater, and exicutes action once it is attached
	 * @param name the name of this task
	 * @param taskRunner the TaskRunner you want to attach this task to 
	 * @param containerName the name used to refrence this task in the TaskRunner
	 * @param action the action to run once this task has been attached to the TaskRunner
	 */
	public Task(String name, TaskRunner taskRunner, String containerName, Action action){
		construct(name, taskRunner, containerName, action);
	}

	/**
	 * Constructor that sets the name of this task and attaches it to taskRunner with the name paramater
	 * @param name the name of this task
	 * @param taskRunner the TaskRunner you want to attach this task to (containerName will equal name)
	 */
	public Task(String name, TaskRunner taskRunner){
		construct(name, taskRunner, name, Action.NONE);
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
	private void construct(String name, TaskRunner taskRunner, String containerName, Action action){
		setName(name);
		if(taskRunner != null)
			taskRunner.addTask(containerName, this, action);
		allTasks.add(this);
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
		taskRunner.runAction(containerName, Action.REMOVE_FROM_QUE);
		taskRunner.runAction(containerName, Action.PAUSE);
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
	 * @param action the action you want to run
	 */
	public void setAction(Runnable action){
		this.action = action;
	}

	/**
	 * gets the action (the thing that is run by taskRunner or manually with {@link Task#run()}) that is currently set - getter method for variable {@link Task#action}
	 * @return the currently set step
	 */
	public Runnable getAction() {
		return action;
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
		else
			Logger.getInstance().addMessage(new Message("can not start " + this + " because no task runner is attached. Either attach a task runner or call the run() method to run directly", Message.Type.WARNING, false), true, true,false);
	}

	public void pause(){
		if(isTaskRunnerAttached())
			taskRunner.removeTaskFromActiveTasks(containerName);
		else
			Logger.getInstance().addMessage(new Message("can not start " + this + " because no task runner is attached. Either attach a task runner or call the run() method to run directly", Message.Type.WARNING, false), true, true,false);
	}

	public void quePause(){
		taskRunner.quePause(containerName);
	}

	public void reset(){}

	public void restart(){}

	/**
	 * runs the action
	 */
	@Override
	public void run(){
		action.run();
	}
	
	public boolean isRunning(){
		return running;
	}

	public boolean isDone(){
		return !running;
	}

	public String getStatusString(String tab, int startTabs){
		String start = "";
		for(int i = 0; i < startTabs; i++){
			start += tab;
		}
		return start + name + " as " + getClass() + ":\n" + 
			start + tab +  "Running: " + running;
	}

	public static LinkedList<Task> getAllTasks(){
		return allTasks;
	}

	public static Task getTaskWithName(String name){
		for(Task t : allTasks)
			if(t.getName().equals(name))
				return t;
		return null;
	}
}