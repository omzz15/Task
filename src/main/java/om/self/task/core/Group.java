package om.self.task.core;

import om.self.structure.KeyedStructure;
import om.self.structure.NamedKeyedStructure;
import om.self.structure.Structure;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * A structure class that can manage and run {@link Runnable} like {@link Task}.
 */
public class Group extends NamedKeyedStructure<String, String, Group> implements Runnable{
    private final Hashtable<String, Runnable> allRunnables = new Hashtable<>();
    private final Hashtable<String, Runnable> activeRunnables = new Hashtable<>();

    //private final LinkedList<Runnable> queuedRunnables = new LinkedList<>();
    //private Runnable currentRunnableFromQue;
    private final LinkedList<Runnable> queuedGroupActions = new LinkedList<>();

    //private String defaultActionName;
    //private boolean runningDefault = false;

    /**
     * Whether this group should automatically be paused and started based on if there are any active runnables
     */
    private boolean autoManage = true;


    //----------CONSTRUCTOR----------//

    /**
     * Basic constructor that just sets the name of this group without attaching parent
     * @param name the name of this group
     */
    public Group(String name){
        construct(name, null, null);
    }

    /**
     * Constructor that sets the name of this group then attaches it to a parent with the parent key as name
     * @param name the name of this group and the key used to identify this to parent
     * @param parent the parent this group is attached to
     */
    public Group(String name, Group parent){
        construct(name, name, parent);
    }

    /**
     * Constructor that sets the name of this group then attaches it to a parent with the parent key as parentKey
     * @param name the name of this group
     * @param parentKey the key used to identify this to parent
     * @param parent the parent this group is attached to
     */
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

    /**
     * gets all the runnables attached to this group
     * @return {@link Group#allRunnables}
     */
    public Hashtable<String, Runnable> getAllRunnables() {
        return allRunnables;
    }

    /**
     * Gets a specific runnable. If nothing is stored with the key then it returns null.
     * @param key the key associated with the runnable you want(aka: {@link KeyedStructure#parentKey})
     * @return the runnable associated with the passed in key
     */
    public Runnable getRunnable(String key){
        return allRunnables.get(key);
    }

    /**
     * gets all active(running) runnables
     * @return {@link Group#activeRunnables}
     */
    public Hashtable<String, Runnable> getActiveRunnables() {
        return activeRunnables;
    }

    /**
     * Gets a specific active runnable. If nothing is stored with the key then it returns null.
     * @param key the key associated with runnable you want(aka: {@link KeyedStructure#parentKey})
     * @return the active runnable associated with the passed in key
     */
    public Runnable getActiveRunnable(String key){
        return activeRunnables.get(key);
    }

//    public LinkedList<Runnable> getQueuedRunnables() {
//        return queuedRunnables;
//    }

//    public Runnable getCurrentRunnableFromQue() {
//        return currentRunnableFromQue;
//    }

    /**
     * gets the queued actions to run whenever {@link Group#run()} is called
     * @return {@link Group#queuedGroupActions}
     */
    public LinkedList<Runnable> getQueuedGroupActions() {
        return queuedGroupActions;
    }

    /**
     * 1
     * @param runnable 1
     */
    public void addToQueuedGroupActions(Runnable runnable){
        queuedGroupActions.add(runnable);
    }

    /**
     * 1
     * @return 1
     */
    public boolean isAutoManageEnabled() {
        return autoManage;
    }

    /**
     * 1
     * @param autoManage 1
     */
    public void setAutoManage(boolean autoManage) {
        this.autoManage = autoManage;
    }

    //----------CHECKS----------//
//    public boolean isRunningDefault() {
//        return runningDefault;
//    }

    /**
     * 1
     * @return 1
     */
    public boolean isRunning() {
        return isParentAttached() && getParent().getActiveRunnable(getParentKey()) != null;
    }

    /**
     * 1
     * @param key 1
     * @return 1
     */
    public boolean isChildRunning(String key){
        return getActiveRunnable(key) != null;
    }


    //----------ADDING and REMOVING----------//
    /**
     * 1
     * @param key 1
     * @param runnable 1
     */
    public void addRunnable(String key, Runnable runnable){
        if(runnable instanceof KeyedStructure<?,?>)
            ((KeyedStructure<String, Group>) runnable).attachParent(key, this);
        else
            allRunnables.put(key, runnable);
    }

    /**
     * 1
     * @param child 1
     */
    public void addRunnable(NamedKeyedStructure<String, String, Group> child){
        addRunnable(child.getName(), (Runnable) child);
    }

    /**
     * 1
     * @param key 1
     */
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

    /**
     * 1
     */
    public void start(){
        if(isParentAttached())
            getParent().runCommand(getParentKey(), Command.START);
        else
            throw new NotImplementedException();
    }

    /**
     * 1
     */
    public void queStart(){
        if(isParentAttached())
            getParent().runCommand(getParentKey(), Command.QUE_START);
    }

    /**
     * 1
     */
    public void pause(){
        if (isParentAttached())
            getParent().runCommand(getParentKey(), Command.PAUSE);
        else
            throw new NotImplementedException();
    }

    /**
     * 1
     */
    public void quePause(){
        if(isParentAttached())
            getParent().runCommand(getParentKey(), Command.QUE_PAUSE);
    }

    /**
     * 1
     * @param key 1
     * @param command 1
     * @param args 1
     * @return 1
     */
    public boolean runCommand(String key, Command command, Object... args){
        switch (command){
            case START: {
                Runnable runnable = getRunnable(key);
                if(runnable == null) return false;
                activeRunnables.put(key, runnable);
                if(isAutoManageEnabled() && isParentAttached() && !isRunning())
                    queStart();
                break;
            }
            case PAUSE: {
                activeRunnables.remove(key);
                if(isAutoManageEnabled() && isParentAttached() && activeRunnables.isEmpty())
                    quePause();
                break;
            }
            case QUE_PAUSE: {
                addToQueuedGroupActions(() -> runCommand(key, Command.PAUSE));
                break;
            }
            case QUE_START:{
                addToQueuedGroupActions(() -> runCommand(key, Command.START));
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
        while(!queuedGroupActions.isEmpty()) queuedGroupActions.removeFirst().run();

        activeRunnables.forEach((k,v) -> v.run());
    }


    public enum Command{
        NONE,
        START,
        PAUSE,
        QUE_PAUSE,
        QUE_START,
        //ADD_TO_QUE,
        //ADD_TO_SPOT_IN_QUE,
        //REMOVE_FROM_QUE,
        ATTACH,
        DETACH
    }
}
