package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

/**
 * 1
 */
public class DelayTask extends TaskEx {
    private long delayStart;

    /**
     * 1
     * @param name 1
     */
    public DelayTask(String name){
        super(name);
    }

    /**
     * 1
     * @param name 1
     * @param parent 1
     */
    public DelayTask(String name, Group parent){
        super(name, parent);
    }

    /**
     * 1
     * @param name 1
     * @param parentKey 1
     * @param parent 1
     */
    public DelayTask(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * 1
     * @param delay 1
     */
    public void addDelay(int delay){
		addStep(() -> {delayStart = System.currentTimeMillis();});
		addStep(() -> (System.currentTimeMillis() - delayStart >= delay));
	}
}
