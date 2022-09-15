package om.self.task.other;

import om.self.task.core.Group;

import java.util.Hashtable;

/**
 * 1
 */
public class GroupEx extends Group {
    private Hashtable<String, Integer> runTimes = new Hashtable<>();

    /**
     * 1
     * @param name 1
     */
    public GroupEx(String name) {
        super(name);
    }
}
