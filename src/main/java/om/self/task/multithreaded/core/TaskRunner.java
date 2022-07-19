package om.self.task.multithreaded.core;

import om.self.task.singlethreaded.core.Task.Step;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;

public class TaskRunner implements Runnable{
	private String name;
	private TaskManager taskManager;
	private String containerName;
	private boolean running = false;
	private boolean resetQueuedTasks = false;
	
	private Hashtable<String, Task> allTasks = new Hashtable<>();
	private Hashtable<String, Task> activeTasks = new Hashtable<>();
	private LinkedList<Task> queuedTasks = new LinkedList<>();
	private LinkedList<Step> quedSteps = new LinkedList<>();

	private Task currentQueued;
	private String defaultTaskName;
	private boolean runningDefault;

	public TaskRunner(String name, boolean resetQueuedTasks, TaskManager taskManager, boolean autoStart, Task... tasks){
		this.name = name;
		this.resetQueuedTasks = resetQueuedTasks;
		
		taskManager.addTaskRunner(name, this, autoStart ? TaskManager.Action.START : TaskManager.Action.NONE);

		addTasks(tasks);
	}

	public TaskRunner(String name, TaskManager taskManager, Task... tasks){
		this.name = name;
		
		taskManager.addTaskRunner(name, this, TaskManager.Action.NONE);

		addTasks(tasks);
	}

	public TaskRunner(String name, Task... tasks){
		this.name = name;

		addTasks(tasks);
	}

	public TaskRunner(Task... tasks){
		this.name = "unnamed";

		addTasks(tasks);
	}

	//---------------Getter and Setter---------------//
	//name
	public void setName(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//running
	void pauseRaw(){
		running = false;
	}

	public void pause(){
		if(isTaskManagerAttached())
			taskManager.runAction(containerName, TaskManager.Action.PAUSE);
		else
			pauseRaw();
	}

	void startRaw(){
		running = true;
	}

	public void start(){
		if(isTaskManagerAttached())
			taskManager.runAction(containerName, TaskManager.Action.START);
		else
			startRaw();
	}

	public boolean isRunning() {
		return running;
	}

	//default task
	public Task getDefaultTask() {
		return getTask(defaultTaskName);
	}

	public void setDefaultTask(String key) {
		defaultTaskName = key;
	}

	/**
	 * sets the default task with a Task object (sets the objects task runner if it is not this task runner) 
	 * @param task the task you want to set as default
	 */
	public void setDefaultTask(Task task){
		if(task == null) return;
		if(task.getTaskRunner() != this)
			task.attachTaskRunner(this, task.getName());
		defaultTaskName = task.getContainerName();
	}

	public void stopDefaultTask(){
		//redundent check if method is private
		if(defaultTaskName == null) return;

		runAction(defaultTaskName, Action.PAUSE);
		runningDefault = false;
	}

	//RestartQueuedTasks
	public void setRestartQueuedTasks(boolean value){
		resetQueuedTasks = value;
	}


	//---------------Attach---------------//
	public void attachTaskManager(TaskManager taskManager, String containerName){
		this.taskManager = taskManager;
		this.containerName = containerName;
		taskManager.addTaskRunnerRaw(containerName, this);
	}

	public void attachTaskManager(TaskManager taskManager){
		this.taskManager = taskManager;
		this.containerName = getName();
		taskManager.addTaskRunnerRaw(containerName, this);
	}

	public void removeTaskManager(){
		taskManager.runAction(containerName, TaskManager.Action.PAUSE);
		taskManager.removeTaskRunnerRaw(containerName);

		this.taskManager = null;
		this.containerName = null;
	}

	public TaskManager getTaskManager(){
		return taskManager;
	}

	public boolean isTaskManagerAttached(){
		return taskManager != null;
	}

	//---------------Actions---------------//
	void addTaskRaw(String key, Task task){
		allTasks.put(key, task);
	}

	void removeTaskRaw(String key){
		allTasks.remove(key);
	}

	/**
	 * adds a task to the list of tasks(allTasks) with a key and optionally attaches the task as a background task(put in backgroundTasks)
	 * @param task the task you want to add/attach to the lists and run
	 * @param action action to run once task has been added
	 */
	public void addTask(String key, Task task, Action action, Object... args){
		if(task == null) return;

		task.attachTaskRunner(this, key);
		runAction(key, action, args);
	}

	public void addTask(Task task, Action action, Object... args){
		if(task == null) return;

		task.attachTaskRunner(this);
		runAction(task.getName(), action, args);
	}

	public void addTask(String key, Task task){
		if(task == null) return;
		task.attachTaskRunner(this, key);
	}

	public void addTask(Task task){
		if(task == null) return;
		task.attachTaskRunner(this);
	}

	public void addTasks(Task... tasks){
		for(Task t : tasks)
			addTask(t);
	}

	void addTaskToActiveTasks(String key, Task value){
		activeTasks.put(key, value);
	}

	/**
	 *
	 * @param key
	 */
	void addTaskToActiveTasks(String key){
		Task t = getTask(key);
		if(t == null) return;
		activeTasks.put(key, t);
	}

	void removeTaskFromActiveTasks(String key){
		activeTasks.remove(key);
	}

	public void quePause(String key){
		queStep(() -> {
			runAction(key, Action.PAUSE);
		});
	}

	public void queStep(Step step){
		quedSteps.add(step);
	}

	public void runAction(String key, Action action, Object... args){
		Task t = allTasks.get(key);
		if(t == null) return;

		switch(action){
			case START:
				addTaskToActiveTasks(t.getContainerName(), t);
				break;
			case ADD_TO_SPOT_IN_QUE:
				if(!(args[0] instanceof Integer)) return;
				if(isRunningDefault()) stopDefaultTask();
				queuedTasks.add((int)args[0], allTasks.get(key));
				break;
			case ADD_TO_QUE:
				runAction(key, Action.ADD_TO_SPOT_IN_QUE, queuedTasks.size());
				break;
			case REMOVE_FROM_QUE:
				queuedTasks.remove(t);
				break;
			case PAUSE:	
				t.pause();
				break;
			case REMOVE_FROM_ALL:
				t.removeTaskRunner();
				break;
			default:
				break;
		}
	}

	//---------------Getters---------------//
	//all task
	public Hashtable<String, Task> getAllTasks(){
		return allTasks;
	}

	//all active tasks
	public Hashtable<String, Task> getActiveTasks(){
		return activeTasks;
	}

	/**
	 * gets the task that is listed in allTasks under the passed in key(name)
	 * @param key the key the task is listed as
	 * @return the task under the listed key
	 */
	public Task getTask(String key){
		return allTasks.get(key);
	}

	//queued
	public LinkedList<Task> getQueuedTasks(){
		return queuedTasks;
	}

	public Task getCurrQueuedTask(){
		return currentQueued;
	}


	//---------------Removers---------------//
	//all
	public void clearAll(){
		clearAllTasks();
		clearQueuedTasks();
	}

	public void clearQueuedTasks(){
		queuedTasks.clear();
	}

	public void clearAllTasks(){
		allTasks.clear();
	}

	public void clearActiveTask(){
		activeTasks.clear();
	}


	//---------------Runners---------------//
	//all
	@Override
	public void run(){
		if(running) 
			runRaw();
	}

	void runRaw(){
		while(!quedSteps.isEmpty()) quedSteps.remove().apply();

		if(currentQueued == null || currentQueued.isDone()){
			if(!queuedTasksDone()){
				currentQueued = queuedTasks.removeFirst();
				if(resetQueuedTasks) currentQueued.reset();
				currentQueued.start();
			} else if(defaultTaskName != null){
				runAction(defaultTaskName, Action.ADD_TO_QUE);
				runningDefault = true;
			}
		}

		activeTasks.forEach((k,t) -> {t.run();});
	}

	//---------------Misc---------------//
	public boolean queuedTasksDone(){
		return queuedTasks.isEmpty();
	}

	public boolean isRunningDefault(){
		return runningDefault;
	}

	public boolean onlyRunningDefault(){
		if(isRunningDefault() && activeTasks.size() == 1) return true;
		return false;
	}

	public boolean isDone(boolean ignoreDefault){
		return activeTasks.isEmpty() || ((ignoreDefault) ? onlyRunningDefault() : false);
	}

	public enum Action{
		NONE,
		START,
		PAUSE,
		ADD_TO_QUE,
		ADD_TO_SPOT_IN_QUE,
		REMOVE_FROM_QUE,
		REMOVE_FROM_ALL
	}

	public String getStatusString(String tab, int startTabs){
		String start = "";
		for(int i = 0; i < startTabs; i++)
			start += tab;

		String out = start + name + ":\n";

		if(allTasks.isEmpty()) return out + start + tab + "No Tasks!";
		
		out += start + tab + "All Tasks:\n";

		for(Entry<String, Task> entry : allTasks.entrySet()){
			out += start + tab + tab + entry.getKey() + ":\n" +
				entry.getValue().getStatusString(tab, startTabs + 3) + "\n";
		}

		out += "\n" + start + tab + "Active Tasks:\n";

		if(activeTasks.isEmpty())
			out += start + tab + tab + "No Active Tasks!\n";
		else{
			for(Entry<String, Task> entry : activeTasks.entrySet()){
				out += start + tab + entry.getKey() + ":\n" +
					entry.getValue().getStatusString(tab, startTabs + 3) + "\n";
			}
		}

		out += "\n" + start + tab + "Queued Tasks:\n";

		if(queuedTasksDone())
			out += start + tab + tab + "No Queued Tasks!\n";
		else{
			for(Task t : queuedTasks)
				out += start + tab + tab + t.getContainerName() + ":\n" +
					t.getStatusString(tab, startTabs + 3) + "\n";
		}
		
		return out;
	}
}