package om.self.task.singlethreaded;

import om.self.task.singlethreaded.core.TaskEx;
import om.self.task.core.TaskRunner;

public class TimedTask extends TaskEx {
    private long startTime;

    public TimedTask(String name) {
        super(name);
    }

    public TimedTask(String name, TaskRunner taskRunner){
		super(name, taskRunner);
	}

    public void addTimedStep(Step step, int time){
        //sanitize input
        if(time <= 0 || step == null) return;

        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(step, () -> (System.currentTimeMillis() - startTime >= time));
    }

    public int getCurrentRunTime(){
        return (int)(System.currentTimeMillis() - startTime);
    }
}
