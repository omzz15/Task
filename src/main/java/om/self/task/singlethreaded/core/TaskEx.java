package om.self.task.singlethreaded.core;

import om.self.task.core.TaskRunner;

import java.util.LinkedList;
import java.util.function.Consumer;

//import logger.Message;

public class TaskEx extends Task {
	private Consumer<Boolean> end;
	private LinkedList<Runnable> steps = new LinkedList<>();
	private LinkedList<Consumer<Boolean>> ends = new LinkedList<>();

	private int task = 0;
	private boolean done = false;

	public TaskEx(String name){
		super(name);
	}

	public TaskEx(String name, TaskRunner taskRunner){
		super(name, taskRunner);
	}

	public void addStep(Step step, EndPoint end){
		steps.add(step);
		ends.add(end);
	}

	public void addStep(EndPoint end){
		steps.add(() -> {});
		ends.add(end);
	}

	public void addStep(Step step){
		steps.add(step);
		ends.add(() -> {return true;});
	}

	public void addStepInIndex(Step step, EndPoint end, int index){
		steps.add(index, step);
		ends.add(index, end);
	}
	

	/**
	 * removes and stops a step based on the index 
	 * @param index the index of the step you want to remove(starts at 0)
	 * @param stopIfActive stops the step if it is currently running
	 */
	public void removeStep(int index, boolean stopIfActive){
		steps.remove(index);
		ends.remove(index);
		if(stopIfActive && task == index){
			setToNextStep();
		}
	}

	public void clear(){
		steps.clear();
		ends.clear();
		reset();
	}

	@Override
	public void reset(){
		task = 0;
		done = false;	
	}

	@Override
	public void restart(){
		reset();
		start();
	}

	@Override
	public void start(){
		if(checkIfDone()) return;
		
		super.start();
		setCurrStep(task);
	}

	@Override
	protected void runRaw(){
		getStep().apply();
		if(end.apply())
			setToNextStep();
	}

	boolean checkIfDone(int curr){
		return curr >= steps.size();
	}

	boolean checkIfDone(){
		done = checkIfDone(task);
		return done;
	}

	@Override
	public boolean isDone(){
		return done;
	}

	public int getCurrentStep(){
		return task;
	}

	public void setCurrStep(int curr){
		if (checkIfDone(curr)) {
			done = true;
			if(isTaskRunnerAttached()) quePause();
			return;
		}
		setStep(steps.get(curr));
		end = ends.get(curr);
		task = curr;
	}

	public void setToNextStep(){
		setCurrStep(task + 1);
	}

	@Override
	public String getStatusString(String tab, int startTabs){
		String start = "";
		for(int i = 0; i < startTabs; i++){
			start += tab;
		}

		return super.getStatusString(tab, startTabs) + "\n" + 
			start + tab + "Current Step: " + task + "\n" +
			start + tab + "Done: " + done;
	}
}
