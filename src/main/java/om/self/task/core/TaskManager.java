package om.self.task.core;

import om.self.task.core.TaskRunner;

import java.util.Hashtable;

public class TaskManager{
	private Hashtable<String, TaskRunner> allTaskRunners = new Hashtable<>();
	private Hashtable<String, TaskRunner> activeTaskRunners = new Hashtable<>();

	public TaskManager(TaskRunner... runners){
		for(TaskRunner tr : runners)
			tr.attachTaskManager(this);
	}

	public TaskManager(){
		new TaskRunner("main",this);
	}

	void addTaskRunnerRaw(String key, TaskRunner task){
		allTaskRunners.put(key, task);
	}

	public void addTaskRunner(String key, TaskRunner taskRunner, Action action){
		if(taskRunner == null) return;

		taskRunner.attachTaskManager(this, key);
		runAction(key, action);
	}

	public void addTaskRunner(TaskRunner taskRunner){
		if(taskRunner == null) return;
		taskRunner.attachTaskManager(this);
	}

	void removeTaskRunnerRaw(String key){
		allTaskRunners.remove(key);
	}

	public TaskRunner getTaskRunner(String key){
		return allTaskRunners.get(key);
	}

	public TaskRunner getMain(){
		return allTaskRunners.get("main");
	}

	public void runAction(String key, Action action){
		TaskRunner tr = allTaskRunners.get(key);
		if(tr == null) return;

		switch(action){
			case NONE:
				break;
			case PAUSE:
				activeTaskRunners.remove(key);
				tr.pauseRaw();
				break;
			case START:
				activeTaskRunners.put(key, tr);
				tr.startRaw();
				break;
			case REMOVE_FROM_ALL:
				tr.removeTaskManager();
				break;
			default:
				break;
		}
	}

	public void run(){
		activeTaskRunners.forEach((k, tr) -> {tr.runRaw();});
	}

	
	public void printCallStack(){
		final String tab = "    ";

		System.out.println("Call Stack:");
		System.out.println(tab + "All Task Runners:");
		if(allTaskRunners.isEmpty()){ 
			System.out.println(tab + tab + "No Task Runners!");
			return;
		}
		else
			allTaskRunners.forEach((k, tr) -> {System.out.println(tr.getStatusString(tab, 2));});

		System.out.println(tab + "Active Task Runners:");
		if(activeTaskRunners.isEmpty()) 
			System.out.println(tab + tab + "No Active Task Runners!");
		else
			activeTaskRunners.forEach((k, tr) -> {System.out.println(tr.getStatusString(tab, 2));});
	}

	

	public enum Action{
		PAUSE,
		START,
		NONE,
		REMOVE_FROM_ALL
	}
}