package om.self.task.other;

import om.self.task.core.Group;

public class DelayTask extends TaskEx{
    private long delayStart;

    public DelayTask(String name){
        super(name);
    }

    public DelayTask(String name, Group parent){
        super(name, parent);
    }

    public DelayTask(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    public void addDelay(int delay){
		if(delay <= 0) return;
		addStep(() -> {delayStart = System.currentTimeMillis();});
		addStep(() -> (System.currentTimeMillis() - delayStart >= delay));
	}
}
