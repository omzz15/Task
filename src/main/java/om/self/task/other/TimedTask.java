package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

public class TimedTask extends TaskEx {
    private long startTime;

    public TimedTask(String name) {
        super(name);
    }

    public TimedTask(String name, Group parent){
		super(name, parent);
	}

    public TimedTask(String name, String parentKey, Group parent){
        super(name, parentKey, parent);
    }

    public void addTimedStep(Runnable step, int time){
        //sanitize input
        if(time <= 0 || step == null) return;

        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(step, () -> (System.currentTimeMillis() - startTime >= time));
    }

    public int getCurrentRunTime(){
        return (int)(System.currentTimeMillis() - startTime);
    }
}
