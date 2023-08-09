package om.self.task.core;

import om.self.structure.NamedStructure;
import om.self.structure.parent.KeyedParentStructureImpl;

import java.util.LinkedList;
import java.util.Map;

import static om.self.task.other.Utils.repeat;

/**
 * A simple task that will execute a Runnable with no input or output.
 * Ways to run are using the run() method or attaching to a TaskRunner.
 */
public class Task extends KeyedParentStructureImpl<String, Group> implements Runnable, NamedStructure<String> {
	/**
	 * Used to log all tasks created if {@link #logTasks} is true
	 */
	private static final LinkedList<Task> allTasks = new LinkedList<>();
	/**
	 * Whether to log all tasks created (this has no barring on the functionality of individual tasks)
	 */
	public static boolean logTasks = false;

	/**
	 * The name of this task
	 */
	private String name;

	/**
	 * the Runnable thing that you want contained inside this task
	 */
	private Runnable runnable;

	/**
	 * if this task automatically starts (put in active runnables) when attached to a group
	 */
	public boolean autoStart = true;
	/**
	 * if this task automatically stops (remove from active runnables) when runnable is run once
	 */
	public boolean autoPause = false;

	/**
	 * this prevents someone from changing the current runnable and the state (whether it is running)
	 */
	public boolean lockState = false;

	//----------CONSTRUCTORS----------//
	/**
	 * Constructor that sets the name of this task and attaches it to a parent Group with the key parentKey
	 * @param name the name of this task
	 * @param parentKey the name used to reference this task in the parent group (not used if the parent parameter is null)
	 * @param parent the Group you want to attach this task to (if null then it won't have a parent)
	 */
	public Task(String name, String parentKey, Group parent){
		setName(name);
		if(parent != null)
			attachParent(parentKey, parent);

		if(logTasks) allTasks.add(this);
	}

	/**
	 * Constructor that sets the name of this task and attaches it to a Group with the key being the same as the name
	 * @param name the name of this task
	 * @param parent the Group you want to attach this task to (parentKey will equal name)
	 */
	public Task(String name, Group parent){
		this(name, name, parent);
	}

	/**
	 * Constructor that just sets the name of this task.
	 * @param name the name of this task
	 */
	public Task(String name){
		this(name, null, null);
	}


	//----------GETTER AND SETTER----------//

	/**
	 * gets the name of this task
	 * @return {@link #name}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * sets the name of this task
	 * @param name the name you want to set this task to
	 * @throws IllegalArgumentException if name is null
	 */
	@Override
	public void setName(String name) {
		if(name == null) throw new IllegalArgumentException("name can not be null!");
		this.name = name; //TODO add auto update option for parent if name changed
	}

	/**
	 * sets the runnable action (the function that is run by taskRunner or run())
	 * @param runnable the runnable action you want to run
	 */
	public void setRunnable(Runnable runnable){
		if(lockState || runnable == null) return;

		this.runnable = runnable;
		if(isParentAttached() && !isRunning() && autoStart)
			runCommand(Group.Command.START);
	}

	/**
	 * gets the runnable action (the thing that is run by taskRunner or manually with {@link Task#run()})
	 * @return the currently set runnable action
	 */
	public Runnable getRunnable() {
		return runnable;
	}


	//----------CHECKS----------//
	/**
	 * checks if this task is running by checking if its parent is running this task <br>
	 * Note: This only works if this task is attached to a parent
	 * @return if this task is running
	 */
	public boolean isRunning(){
		return isParentAttached() && getParent().isChildRunning(getParentKey());
	}

	/**
	 * Just returns the opposite{@link #isRunning()}
	 * @return if this task is done (not running)
	 */
	public boolean isDone(){
		return !isRunning();
	}


	//----------IMPLEMENT KeyedParentStructure----------//

	/**
	 * Attaches this task to a parent group with the key then attaches this task as a child to the parent group using the same key
	 * @param key the key used to reference this task in the parent group
	 * @param parent the parent group you want to attach this task to
	 */
	@Override
	public void attachParent(String key, Group parent) {
		super.attachParent(key, parent);
		parent.attachChild(key, this);
	}

	/**
	 * Same as {@link #attachParent(String, Group)} but it uses the name as the key
	 * @param parent the parent group you want to attach this task to
	 */
	public void attachParent(Group parent){
		attachParent(name, parent);
	}

	/**
	 * detaches this task from its parent group then detaches the parent group from this task
	 */
	@Override
	public void detachParent() {
		if(!isParentAttached()) return;

		getParent().detachChild(getParentKey());
		super.detachParent();
	}


	//----------IMPLEMENT Runnable----------//
	/**
	 * runs the action
	 */
	@Override
	public void run(){
		runnable.run();
		if(isRunning() && autoPause)
			runCommand(Group.Command.PAUSE);
	}


	//----------Command----------//

	/**
	 * Calls {@link Group#runKeyedCommand(String, Group.Command, Map.Entry[])} on the parent <br>
	 * Note: This only works if this task is attached to a parent
	 * @param command the command you want to run
	 * @param args the arguments for the command
	 * @return if the command was run successfully
	 */
	public boolean runCommand(Group.Command command, Map.Entry<String, Object>... args) {
		if(isParentAttached()) return getParent().runKeyedCommand(getParentKey(), command, args);
		return false;
	}


	//----------INFO----------//

	/**
	 * Gets the base info of this task
	 * @param tab the style of tabbing you want to use
	 * @param start the start of the string
	 * @return the base info of this task
	 */
	protected StringBuilder getBaseInfo(String tab, String start){
		StringBuilder str = new StringBuilder(start);
		str.append(getName() + " Info:");
		str.append("\n");
		str.append(start + tab + "Type: " + getClass().getSimpleName());
		str.append("\n");
		str.append(start + tab + "Status: ");
		if(!isParentAttached())
			str.append("No Parent");
		else if (isRunning())
			str.append("Running");
		else
			str.append("Not Running");
		return str;
	}

	/**
	 * Gets the info of this task
	 * @param tab the style of tabbing you want to use
	 * @param startTabs the number of tabs you want to start with
	 * @param extend if you want to get extra info
	 * @return the info of this task
	 */
	public String getInfo(String tab, int startTabs, boolean extend){
		String start = repeat(tab, startTabs);
		StringBuilder str = getBaseInfo(tab, start);
		if(extend){
			str.append("\n");
			str.append(start + tab +  "Auto Start: " + autoStart);
			str.append("\n");
			str.append(start + tab +  "Auto Pause: " + autoPause);
		}
		return str.toString();
	}

	/**
	 * Gets the info of this task by calling {@link #getInfo(String, int, boolean)} with the default values (tab = "\t", startTabs = 0, extend = false)
	 * @return the info of this task
	 */
	@Override
	public String toString(){
		return getInfo("\t", 0, false);
	}


	//----------STATIC METHODS----------//
	/**
	 * gets all the tasks created if {@link #logTasks} is true
	 * @return all the tasks created
	 */
	public static LinkedList<Task> getAllTasks(){
		return allTasks;
	}

	/**
	 * gets a task with the name
	 * @param name the name of the task you want to get
	 * @return the task with the name or null if no task has that name
	 */
	public static Task getTaskWithName(String name){
		for(Task t : allTasks)
			if(t.getName().equals(name))
				return t;
		return null;
	}
}