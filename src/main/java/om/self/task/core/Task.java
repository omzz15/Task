package om.self.task.core;
import java.util.LinkedList;

import om.self.logger.Logger;
import om.self.logger.Message;
import om.self.task.structure.KeyedStructure;
import org.apache.commons.lang3.NotImplementedException;

/**
 * A simple task that will exicute a Lambda Function with no input or output.
 * Ways to run are using the run() method or attaching to a TaskRunner.
 */
public class Task extends KeyedStructure<String, Group> implements Runnable{

	private static LinkedList<Task> allTasks = new LinkedList<>();
	public static boolean logTasks = false;

	private String name;
	/**
	 * the Runnable thing that you want contained inside this task
	 * <p></p>
	 * NOTE: this may be run inside a different thread if you are using multithreaded components
	 */
	private Runnable runnable;


	//----------CONSTRUCTORS----------//
	/**
	 * Constructor that sets the name of this task and attaches it to a parent Group with the key parentKey
	 * @param name the name of this task
	 * @param parentKey the name used to reference this task in the parent group (not used if the parent parameter is null)
	 * @param parent the Group you want to attach this task to (if null then it won't have a parent)
	 */
	public Task(String name, String parentKey, Group parent){
		construct(name, parentKey, parent);
	}

	/**
	 * Constructor that sets the name of this task and attaches it to a Group with the key being the same as the name
	 * @param name the name of this task
	 * @param parent the Group you want to attach this task to (parentKey will equal name)
	 */
	public Task(String name, Group parent){
		construct(name, name, parent);
	}

	/**
	 * Constructor that just sets the name of this task.
	 * @param name the name of this task
	 */
	public Task(String name){
		construct(name, null, null);
	}

	/**
	 * the method called by constructors to initialize variables and optionally set parent group
	 * @param name the name of this task
	 * @param parentKey the name used to reference this task in the parent group (not used if the parent parameter is null)
	 * @param parent the Group you want to attach this task to (if null then it won't have a parent)
	 */
	private void construct(String name, String parentKey, Group parent){
		setName(name);
		if(parent != null)
			attachParent(parentKey, parent);

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
	@Override
	public void onAttached() {
		getParent().getAllRunnables().put(getParentKey(), this);
	}

	@Override
	public void onDetach() {
		getParent().getAllRunnables().remove(getParentKey());
	}

	//----------CHECKS----------//
	public boolean isGroupAttached(){
		return getParent() != null;
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