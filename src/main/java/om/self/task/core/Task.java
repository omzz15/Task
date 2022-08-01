package om.self.task.core;

import java.util.LinkedList;

import om.self.structure.NamedKeyedStructure;
import om.self.task.command.Commandable;

/**
 * A simple task that will execute a Lambda Function with no input or output.
 * Ways to run are using the run() method or attaching to a TaskRunner.
 */
public class Task extends NamedKeyedStructure<String, String, Group> implements Runnable, Commandable<Group.Command, Object> {
	private static final LinkedList<Task> allTasks = new LinkedList<>();
	/**
	 * 1
	 */
	public static boolean logTasks = false;
	/**
	 * the Runnable thing that you want contained inside this task
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


	//----------IMPLEMENT NamedKeyedStructure----------//
	@Override
	protected void onAttached() {
		getParent().getAllRunnables().put(getParentKey(), this);
	}

	@Override
	protected void onDetach() {
		getParent().getAllRunnables().remove(getParentKey());
	}


	//----------IMPLEMENT Runnable----------//
	/**
	 * runs the action
	 */
	@Override
	public void run(){
		runnable.run();
	}


	//----------IMPLEMENT Commandable----------//
	@Override
	public boolean runCommand(Group.Command command, Object... args) {
		if(isParentAttached()) getParent().runKeyedCommand(getParentKey(), command, args);
		return false;
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
		return start + getName() + " as " + getClass() + ":\n" +
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