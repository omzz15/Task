package om.self.task.other;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;

/**
 * 1
 */
public class Que extends TaskEx {
    //----------CONSTRUCTORS----------//

    /**
     * 1
     * @param name 1
     */
    public Que(String name) {
        super(name);
    }

    /**
     * 1
     * @param name 1
     * @param parent 1
     */
    public Que(String name, Group parent) {
        super(name, parent);
    }

    /**
     * 1
     * @param name 1
     * @param queuedRunnables 1
     */
    public Que(String name, Runnable... queuedRunnables){
        super(name);
        for (Runnable runnable: queuedRunnables) {
            addStep(runnable);
        }
    }

    /**
     * 1
     * @param name 1
     * @param parentKey 1
     * @param parent 1
     */
    public Que(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }


    @Override
    public void setToNextStep() {
        removeStep(0, true);
    }
}
