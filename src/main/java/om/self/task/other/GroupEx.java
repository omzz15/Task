package om.self.task.other;

import om.self.task.core.Group;

import java.util.Hashtable;


public class GroupEx extends Group {
    private Hashtable<String, Integer> runTimes = new Hashtable<>();

    public GroupEx(String name) {
        super(name);
    }
}
