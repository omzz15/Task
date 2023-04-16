package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

import java.util.function.Supplier;

/**
 * 1
 */
public class TimedTask extends TaskEx {
    private long startTime;

    /**
     * 1
     * @param name 1
     */
    public TimedTask(String name) {
        super(name);
    }

    /**
     * 1
     * @param name 1
     * @param parent 1
     */
    public TimedTask(String name, Group parent){
		super(name, parent);
	}

    /**
     * 1
     * @param name 1
     * @param parentKey 1
     * @param parent 1
     */
    public TimedTask(String name, String parentKey, Group parent){
        super(name, parentKey, parent);
    }

    /**
     * 1
     * @param step 1
     * @param time 1
     */
    public void addTimedStep(Runnable step, int time){
        //sanitize input
        if(time <= 0 || step == null) return;

        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(step, () -> (System.currentTimeMillis() - startTime >= time));
    }

    public void addTimedStep(Runnable step, Supplier<Boolean> end, int time){
        //sanitize input
        if(time <= 0 || step == null) return;

        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(step, () -> (end.get() || (System.currentTimeMillis() - startTime >= time)));
    }

    public void addDelay(int delay){
        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(() -> (System.currentTimeMillis() - startTime >= delay));
    }

    public void addConditionalDelay(int delay, Supplier<Boolean> keepDelaying) {
        addStep(() -> {startTime = System.currentTimeMillis();});
        addStep(() -> (System.currentTimeMillis() - startTime >= delay || !keepDelaying.get()));
    }

    /**
     * 1
     * @return 1
     */
    public int getCurrentRunTime(){
        return (int)(System.currentTimeMillis() - startTime);
    }
}
