package om.self.task.core;
import java.util.LinkedList;

import om.self.structure.KeyedStructure;
import org.apache.commons.lang3.NotImplementedException;

/**
 * A simple task that will execute a Lambda Function with no input or output.
 * Ways to run are using the run() method or attaching to a TaskRunner.
 */
public class Task extends KeyedStructure<String, Group> implements Runnable{

	private static final LinkedList<Task> allTasks = new LinkedList<>();
	/**
	 * 1
	 */
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
	 * @return {@link Task#name}
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
	protected void onAttached() {
		getParent().getAllRunnables().put(getParentKey(), this);
	}

	@Override
	protected void onDetach() {
		getParent().getAllRunnables().remove(getParentKey());
	}

	//----------CHECKS----------//

	/**
	 * 1
	 * @return 1
	 */
	public boolean isRunning(){
		return isParentAttached() && getParent().isChildRunning(getParentKey());
	}

	/**
	 * 1
	 * @return 1
	 */
	public boolean isDone(){
		return !isRunning();
	}


	//----------ACTIONS----------//
	/**
	 * 1
	 */
	public void start(){
		if(isParentAttached())
			getParent().runCommand(getParentKey(), Group.Command.START);
		else
			throw new NotImplementedException();
	}

	/**
	 * 1
	 */
	public void pause(){
		if(isParentAttached())
			getParent().runCommand(getParentKey(), Group.Command.PAUSE);
		else
			throw new NotImplementedException();
	}

	/**
	 * 1
	 */
	public void quePause(){
		if(isParentAttached())
			getParent().runCommand(getParentKey(), Group.Command.QUE_PAUSE);
		else
			throw new NotImplementedException();
	}

	/**
	 * 1
	 */
	public void queStart(){
		if(isParentAttached())
			getParent().runCommand(getParentKey(), Group.Command.QUE_START);
		else
			throw new NotImplementedException();
	}

	//----------RUN----------//
	/**
	 * runs the action
	 */
	@Override
	public void run(){
		runnable.run();
	}


	//----------INFO----------//

	/**
	 * 1
	 * @param tab 1
	 * @param startTabs 1
	 * @return 1
	 */
	public String getStatusString(String tab, int startTabs){
		StringBuilder start = new StringBuilder();
		start.append(tab.repeat(startTabs));
		return start + name + " as " + getClass() + ":\n" +
			start + tab +  "Running: " + isRunning();
	}

	@Override
	public String toString(){
		return getStatusString("\t", 0);
	}


	//----------STATIC METHODS----------//

	/**
	 * 1
	 * @return 1
	 */
	public static LinkedList<Task> getAllTasks(){
		return allTasks;
	}

	/**
	 * 1
	 * @param name 1
	 * @return 1
	 */
	public static Task getTaskWithName(String name){
		for(Task t : allTasks)
			if(t.getName().equals(name))
				return t;
		return null;
	}
}