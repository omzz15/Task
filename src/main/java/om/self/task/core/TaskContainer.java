package om.self.task.core;

public class TaskContainer extends Task{
    public TaskContainer(String name, String parentKey, Group parent) {
        super(name, parentKey, parent);
    }

    public TaskContainer(String name, Group parent) {
        super(name, parent);
    }

    public TaskContainer(String name) {
        super(name);
    }

    @Override
    public void setRunnable(Runnable runnable) {
        super.setRunnable(runnable);
    }


}
