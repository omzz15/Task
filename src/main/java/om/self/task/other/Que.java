package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

/**
 * A version of {@link TaskEx} that adds steps to a queue and removes them as they are completed
 */
public class Que extends TaskEx {
    //----------CONSTRUCTORS----------//

    /**
     * Constructor that just sets the name of this task
     * @param name the name of this task
     */
    public Que(String name) {
        super(name);
    }

    /**
     * Constructor that sets the name and parent of this task
     * @param name the name of this task
     * @param parent the parent of this task
     */
    public Que(String name, Group parent) {
        super(name, parent);
    }

    /**
     * Constructor that sets the name and queues some runnables
     * @param name the name of this task
     * @param queuedRunnables the runnables to queue
     */
    public Que(String name, Runnable... queuedRunnables){
        super(name);
        for (Runnable runnable: queuedRunnables) {
            addStep(runnable);
        }
    }

    /**
     * Constructor that sets the name, parent key, and parent of this task
     * @param name the name of this task
     * @param parentKey the parent key associated with the parent of this task
     * @param parent the parent of this task
     */
    public Que(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    /**
     * An edit of {@link TaskEx#setToNextStep()} that removes the step instead of setting the current step to the next step to turn this into a queue
     */
    @Override
    public void setToNextStep() {
        removeStep(0, true);
    }
}
