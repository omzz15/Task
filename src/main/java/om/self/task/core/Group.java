package om.self.task.core;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

public class Group implements Runnable{
    private String name;
    private Group group;
    private String groupKey;

    private final Hashtable<String, Runnable> allRunnables = new Hashtable<>();
    private final Hashtable<String, Runnable> activeRunnables = new Hashtable<>();

    private final LinkedList<Runnable> queuedRunnables = new LinkedList<>();
    private Runnable currentRunnableFromQue;
    private final LinkedList<Runnable> queuedGroupActions = new LinkedList<>();

    private String defaultActionName;
    private boolean runningDefault = false;


    //----------CONSTRUCTOR----------//
    public Group(String name, Runnable... runnables){
        construct(name, null, null, null, runnables);
    }

    public Group(String name, Group group, Runnable... runnables){
        construct(name, group, name, Command.NONE, runnables);
    }

    public Group(String name, Group group, String groupKey, Command command, Runnable... runnables){
        construct(name, group, groupKey, command, runnables);
    }

    private void construct(String name, Group group, String groupKey, Command command, Object... args){
        this.name = name;
        if(group != null) {
            attachToGroup(groupKey, group);
            if (command != null)
                runCommand(groupKey, command, args);
        }
    }


    //----------GETTER and SETTER----------//
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public String getDefaultActionName() {
        return defaultActionName;
    }

    public void setDefaultActionName(String defaultActionName) {
        this.defaultActionName = defaultActionName;
    }

    public boolean isRunningDefault() {
        return runningDefault;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public boolean isRunning() {
        if(is)
    }

    public Hashtable<String, Runnable> getAllRunnables() {
        return allRunnables;
    }

    public Runnable getRunnable(String key){
        return allRunnables.get(key);
    }

    public Hashtable<String, Runnable> getActiveRunnables() {
        return activeRunnables;
    }

    public LinkedList<Runnable> getQueuedRunnables() {
        return queuedRunnables;
    }

    public Runnable getCurrentRunnableFromQue() {
        return currentRunnableFromQue;
    }

    public LinkedList<Runnable> getQueuedGroupActions() {
        return queuedGroupActions;
    }

    //----------ADDING NEW----------//
    public void addRunnable(String key, Runnable runnable, Command command, Object... args){
        if(runnable instanceof Group){
            Group group = (Group) runnable;
            group.group = this.group;
            group.groupKey = key;
        }else if(runnable instanceof  Task){
            Task task = (Task) runnable;
            task.se
        }
        allRunnables.put(groupKey, runnable);
    }

    public void addRunnable(String key, Runnable runnable){
        allRunnables.put(key, runnable);
    }

    public void attachToGroup(String groupKey, Group group){
        this.group = group;
        this.groupKey = groupKey;
        group.addRunnable(groupKey, this);
    }

    public void attachToGroup(Group group){
        attachToGroup(name, group);
    }

    //----------ACTIONS----------//
    public void start(){}

    public void pause(){}

    public void quePause(){}

    public void reset(){}

    public boolean runCommand(String key, Command command, Object... args){
        switch (command){
            case START: {
                Runnable runnable = getRunnable(key);
                if(runnable == null) return false;
                activeRunnables.put(key, runnable);
                break;
            }
            case PAUSE: {
                activeRunnables.remove(key);
                break;
            }
            case QUE_PAUSE: {
                queuedGroupActions.add(() -> runCommand(key, Command.PAUSE));
                break;
            }
            case ADD_TO_QUE: {
                Runnable runnable = getRunnable(key);
                if (runnable == null) return false;
                queuedRunnables.add(runnable);
                break;
            }
            case ADD_TO_SPOT_IN_QUE:{
                Runnable runnable = getRunnable(key);
                if (runnable == null) return false;
                try {
                    int location = (Integer) args[1];
                    queuedRunnables.add(location, runnable);
                }
                catch (Exception e){
                    return false;
                }
                break;
            }
            case REMOVE_FROM_QUE:{
                Runnable runnable = getRunnable(key);
                return queuedRunnables.remove(runnable);
            }
            case REMOVE_FROM_ALL:{
                Runnable runnable = getRunnable(key);
                if (runnable == null) return false;
                queuedRunnables.remove(runnable);
                activeRunnables.remove(key);
                allRunnables.remove(key);
                break;
            }
            case NONE:
                return true;
            default:
                return false;
        }
        return true;
    }

    //----------RUN----------//
    @Override
    public void run(){
        queuedGroupActions.forEach((runnable -> runnable.run()));
        throw new NotImplementedException();
    }

    public enum Command{
        NONE,
        START,
        PAUSE,
        QUE_PAUSE,
        ADD_TO_QUE,
        ADD_TO_SPOT_IN_QUE,
        REMOVE_FROM_QUE,
        REMOVE_FROM_ALL
    }
}
