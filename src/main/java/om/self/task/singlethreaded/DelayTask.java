package om.self.task.singlethreaded;

import om.self.task.singlethreaded.core.TaskEx;
import om.self.task.core.TaskRunner;

public class DelayTask extends TaskEx {
    private long delayStart;

    public DelayTask(String name){
        super(name);
    }

    public DelayTask(String name, TaskRunner taskRunner){
        super(name, taskRunner);
    }

    public void addDelay(int delay){
		if(delay <= 0) return;
		addStep(() -> {delayStart = System.currentTimeMillis();});
		addStep(() -> (System.currentTimeMillis() - delayStart >= delay));
	}
}
