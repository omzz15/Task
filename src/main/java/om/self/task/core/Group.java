package om.self.task.core;

import om.self.task.structure.KeyedStructure;
import om.self.task.structure.NamedKeyedStructure;
import om.self.task.structure.Structure;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Hashtable;
import java.util.LinkedList;

public class Group extends NamedKeyedStructure<String, String, Group> implements Runnable{
    private final Hashtable<String, Runnable> allRunnables = new Hashtable<>();
    private final Hashtable<String, Runnable> activeRunnables = new Hashtable<>();

    //private final LinkedList<Runnable> queuedRunnables = new LinkedList<>();
    //private Runnable currentRunnableFromQue;
    private final LinkedList<Runnable> queuedGroupActions = new LinkedList<>();

    //private String defaultActionName;
    //private boolean runningDefault = false;


    //----------CONSTRUCTOR----------//
    public Group(String name){
        construct(name, null, null);
    }

    public Group(String name, Group parent){
        construct(name, name, parent);
    }

    public Group(String name, String parentKey, Group parent){
        construct(name, parentKey, parent);
    }

    private void construct(String name, String parentKey, Group group){
        setName(name);
        if(group != null) {
            attachParent(parentKey, group);
        }
    }


    //----------GETTER and SETTER----------//
//    public String getDefaultActionName() {
//        return defaultActionName;
//    }

//    public void setDefaultActionName(String defaultActionName) {
//        this.defaultActionName = defaultActionName;
//    }

    public Hashtable<String, Runnable> getAllRunnables() {
        return allRunnables;
    }

    public Runnable getRunnable(String key){
        return allRunnables.get(key);
    }

    public Hashtable<String, Runnable> getActiveRunnables() {
        return activeRunnables;
    }

    public Runnable getActiveRunnable(String key){
        return activeRunnables.get(key);
    }

//    public LinkedList<Runnable> getQueuedRunnables() {
//        return queuedRunnables;
//    }

//    public Runnable getCurrentRunnableFromQue() {
//        return currentRunnableFromQue;
//    }

    public LinkedList<Runnable> getQueuedGroupActions() {
        return queuedGroupActions;
    }


    //----------CHECKS----------//
//    public boolean isRunningDefault() {
//        return runningDefault;
//    }

    public boolean isRunning() {
        return isParentAttached() && getParent().getActiveRunnable(getParentKey()) != null;
    }

    public boolean isChildRunning(String key){
        return getActiveRunnable(key) != null;
    }

    //----------ADDING and REMOVING----------//
    public void addRunnable(String key, Runnable runnable){
        if(runnable instanceof KeyedStructure<?,?>)
            ((KeyedStructure<String, Group>) runnable).attachParent(key, this);
        else
            allRunnables.put(key, runnable);
    }

    public void addRunnable(NamedKeyedStructure<String, String, Group> child){
        addRunnable(child.getName(), (Runnable) child);
    }

    public void removeRunnable(String key){
        Runnable r = allRunnables.remove(key);
        if(r instanceof Structure<?>)
            ((Structure<?>)r).detachParent();
        else {
//            queuedRunnables.remove(r);
            activeRunnables.remove(key);
        }
    }


    //----------IMPLEMENTED METHODS----------//
    @Override
    protected void onAttached() {
        getParent().allRunnables.put(getParentKey(), this);
    }

    @Override
    protected void onDetach() {
        Group parent = getParent();
        String key = getParentKey();

//        parent.queuedRunnables.remove(this);
        parent.activeRunnables.remove(key);
        parent.allRunnables.remove(key);
    }


    //----------ACTIONS----------//
    public void start(){
        if(isParentAttached())
            getParent().runCommand(getParentKey(), Command.START);
        else
            throw new NotImplementedException();
    }

    public void pause(){
        if (isParentAttached())
            getParent().runCommand(getParentKey(), Command.PAUSE);
        else
            throw new NotImplementedException();
    }

    public void quePause(){
        if(isParentAttached())
            getParent().runCommand(getParentKey(), Command.QUE_PAUSE);
    }

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
//            case ADD_TO_QUE: {
//                Runnable runnable = getRunnable(key);
//                if (runnable == null) return false;
//                queuedRunnables.add(runnable);
//                break;
//            }
//            case ADD_TO_SPOT_IN_QUE:{
//                try {
//                    Runnable runnable = getRunnable(key);
//                    int location = (Integer) args[0];
//                    queuedRunnables.add(location, runnable);
//                }
//                catch (Exception e){
//                    return false;
//                }
//                break;
//            }
//            case REMOVE_FROM_QUE:{
//                Runnable runnable = getRunnable(key);
//                if (runnable == null) return false;
//                queuedRunnables.remove(runnable);
//            }
            case ATTACH:{
                try{
                    addRunnable(key, (Runnable) args[0]);
                }catch (Exception ignore){
                    return false;
                }
            }
            case DETACH:{
                removeRunnable(key);
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

        activeRunnables.forEach((k,v) -> v.run());
    }

    public enum Command{
        NONE,
        START,
        PAUSE,
        QUE_PAUSE,
        //ADD_TO_QUE,
        //ADD_TO_SPOT_IN_QUE,
        //REMOVE_FROM_QUE,
        ATTACH,
        DETACH
    }
}
