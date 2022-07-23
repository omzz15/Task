package om.self.task.core;
import java.util.LinkedList;

import om.self.logger.Logger;
import om.self.logger.Message;
import org.apache.commons.lang3.NotImplementedException;

/**
 * A simple task that will exicute a Lambda Function with no input or output.
 * Ways to run are using the run() method or attaching to a TaskRunner.  
 */
public class Task implements Runnable{

	private static LinkedList<Task> allTasks = new LinkedList<>();
	public static boolean logTasks = false;

	private String name;
	private Group group;
	/**
	 * the name used to identify this task in the group
	 */
	private String groupKey;
	/**
	 * the Runnable thing that you want contained inside this task
	 * <p></p>
	 * NOTE: this may be run inside a different thread if you are using multithreaded components
	 */
	private Runnable runnable;


	//----------CONSTRUCTORS----------//
	/**
	 * Construtor that sets the name of this task, attaches it to taskRunner with the taskRunnerKey paramater, and exicutes command once it is attached
	 * @param name the name of this task
	 * @param taskRunner the TaskRunner you want to attach this task to 
	 * @param taskRunnerKey the name used to refrence this task in the TaskRunner
	 * @param command the command to run once this task has been attached to the TaskRunner
	 */
	public Task(String name, Group group, String groupKey, Group.Command command){
		construct(name, group, groupKey, command);
	}

	/**
	 * Constructor that sets the name of this task and attaches it to taskRunner with the name paramater
	 * @param name the name of this task
	 * @param taskRunner the TaskRunner you want to attach this task to (taskRunnerKey will equal name)
	 */
	public Task(String name, Group group){
		construct(name, group, name, Group.Command.NONE);
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
	 * @param command the command to run once this task has been attached to the TaskRunner (not used if taskRunner is null)
	 */
	private void construct(String name, Group group, String groupKey, Group.Command command){
		setName(name);
		//if(taskRunner != null)
		//	taskRunner.addTask(containerName, this, command);
		throw new NotImplementedException();
		if(logTasks) allTasks.add(this);
	}


	//----------GETTER AND SETTER----------//
	/**
	 * sets the name of this task
	 * @param name the name you want to set this task to
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * gets the name of this task
	 */
	public String getName() {
		return name;
	}

	/**
	 * get the current attached group (returns null if no group is attached)
	 * @return the attached group
	 */
	public Group getGroup(){
		return group;
	}

	/**
	 * gets the key(name) used to identify this task in the attached group
	 * @return the key or null if no group is attached
	 */
	public String getGroupKey(){
		return groupKey;
	}

	/**
	 * sets the runnable action (the function that is run by taskRunner or run())
	 * @param runnable the runnable action you want to run
	 */
	public void setRunnable(Runnable runnable){
		this.runnable = runnable;
	}

	/**
	 * gets the runnable action (the thing that is run by taskRunner or manually with {@link Task#run()})
	 * @return the currently set runnable action
	 */
	public Runnable getRunnable() {
		return runnable;
	}


	//----------ATTACH and DETACH----------//
	public void attachToGroup(String groupKey, Group group){
		this.group = group;
		this.groupKey = groupKey;
		group.addRunnable(groupKey, this);
	}

	public void attachToGroup(Group group){
		attachToGroup(name, group);
	}

	public void detachFromGroup(){
		throw new NotImplementedException();
	}


	//----------CHECKS----------//
	public boolean isGroupAttached(){
		return group != null;
	}

	public boolean isRunning(){
		throw new NotImplementedException();
	}

	public boolean isDone(){
		return !isRunning();
	}


	//----------ACTIONS----------//
	public void start(){
		if(isGroupAttached())
			throw new NotImplementedException();
		else
			Logger.getInstance().addMessage(new Message("can not start " + this + " because no task runner is attached. Either attach a task runner or call the run() method to run directly", Message.Type.WARNING, false), true, true,false);
	}

	public void pause(){
		if(isGroupAttached())
			throw new NotImplementedException();
		else
			Logger.getInstance().addMessage(new Message("can not pause " + this + " because no task runner is attached. Either attach a task runner or call the run() method to run directly", Message.Type.WARNING, false), true, true,false);
	}

	public void quePause(){
		throw new NotImplementedException();
	}

	public void reset(){}

	public void restart(){}


	//----------RUN----------//
	/**
	 * runs the action
	 */
	@Override
	public void run(){
		runnable.run();
	}


	//----------INFO----------//
	public String getStatusString(String tab, int startTabs){
		String start = "";
		for(int i = 0; i < startTabs; i++){
			start += tab;
		}
		return start + name + " as " + getClass() + ":\n" + 
			start + tab +  "Running: " + running;
	}

	@Override
	public String toString(){
		return getStatusString("\t", 0);
	}


	//----------STATIC METHODS----------//
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