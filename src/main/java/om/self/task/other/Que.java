package om.self.task.other;

import om.self.task.core.Group;

public class Que extends TaskEx{
    //----------CONSTRUCTORS----------//
    public Que(String name) {
        super(name);
    }

    public Que(String name, Group parent) {
        super(name, parent);
    }

    public Que(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }


    @Override
    public void setToNextStep() {
        removeStep(0, true);
    }
}
