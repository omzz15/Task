package om.self.task.core;

import om.self.structure.NamedStructure;
import om.self.structure.bidirectional.KeyedBidirectionalStructure;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

/**
 * A structure class that can manage and run {@link Runnable} like {@link Task}.
 */
public class Group extends KeyedBidirectionalStructure<String, Group, Runnable> implements Runnable, NamedStructure<String>{
    private String name;
    private final Hashtable<String, Runnable> activeRunnables = new Hashtable<>();
    private final LinkedList<Runnable> queuedGroupActions = new LinkedList<>();

    /**
     * Whether this group should automatically be paused and started based on if there are any active runnables
     */
    public boolean autoManage = true;

    /**
     * the number of maximum active runnables at a time(-1 means infinity)
     */
    private int maxActiveRunnables = -1;

    /**
     * 1
     */
    public boolean forceActiveRunnablesDefault = false;


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
        this.name = name;
        if(group != null) {
            attachParent(parentKey, group);
        }
    }


    //----------GETTER and SETTER----------//
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets all active(running) runnables
     * @return {@link Group#activeRunnables}
     * @apiNote DO NOT use this to add or remove from the active runnables. Use {@link Group#runKeyedCommand(String, Command, Object...)} because there are checks that need to be run.
     */
    public Hashtable<String, Runnable> getActiveRunnables() {
        return activeRunnables;
    }

    /**
     * Gets a specific active runnable. If nothing is stored with the key then it returns null.
     * @param key the key associated with runnable you want
     * @return the active runnable associated with the passed in key
     */
    public Runnable getActiveRunnable(String key){
        return activeRunnables.get(key);
    }

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
    public int getMaxActiveRunnables() {
        return maxActiveRunnables;
    }

    /**
     * sets the maximum number of runnables that can run at once(anything less than 1 is infinity)
     * @param maxActiveRunnables the maximum active runnables
     */
    public void setMaxActiveRunnables(int maxActiveRunnables) {
        if(maxActiveRunnables == 0) maxActiveRunnables = -1;
        this.maxActiveRunnables = maxActiveRunnables;
    }


    //----------CHECKS----------//
    /**
     * 1
     * @return 1
     */
    public boolean isRunning() {
        return isParentAttached() && getParent().getActiveRunnable(getParentKey()) != null;
    }

    /**
     * 1
     * @return 1
     */
    public boolean isDone(){
        return activeRunnables.isEmpty() && queuedGroupActions.isEmpty();
    }

    /**
     * 1
     * @param key 1
     * @return 1
     */
    public boolean isChildRunning(String key){
        return getActiveRunnable(key) != null;
    }


    //----------IMPLEMENT Structure methods----------//
    @Override
    public void onChildDetach(String key, Runnable child) {
        activeRunnables.remove(key);
    }

    /**
     * 1
     * @param group 1
     */
    public void attachParent(Group group) {
        attachParent(name, group);
    }

    /**
     *
     * @param child
     */
    public void attachChild(NamedStructure<String> child){
        attachChild(child.getName(), (Runnable) child);
    }

    //----------Commands----------//
    /**
     * 1
     * @param key 1
     * @param command 1
     * @param args 1
     * @return 1
     */
    public boolean runKeyedCommand(String key, Command command, Object... args){
        switch (command){
            case START: {
                if(activeRunnables.size() == maxActiveRunnables){
                    boolean force = forceActiveRunnablesDefault;
                    try{
                        force = (boolean) args[0];
                    } catch (Exception ignore){}

                    if(!force) return false;

                    activeRunnables.remove(activeRunnables.keys().nextElement());
                }

                return startRunnable(key);
            }
            case PAUSE: {
                activeRunnables.remove(key);
                if(autoManage && isParentAttached() && activeRunnables.isEmpty())
                    runCommand(Command.QUE_PAUSE);
                break;
            }
            case QUE_PAUSE: {
                Runnable pause = () -> runKeyedCommand(key, Command.PAUSE);
                if(!queuedGroupActions.contains(pause))
                    addToQueuedGroupActions(pause);
                break;
            }
            case QUE_START:{
                Runnable start = () -> runKeyedCommand(key, Command.START);
                if(!queuedGroupActions.contains(start))
                    addToQueuedGroupActions(start);
                break;
            }
            case NONE:
                return true;
            default:
                return false;
        }
        return true;
    }

    private boolean startRunnable(String key){
        Runnable runnable = getChild(key);
        if(runnable == null) return false;
        activeRunnables.put(key, runnable);
        if(autoManage && isParentAttached() && !isRunning())
            runCommand(Command.QUE_START);
        return true;
    }

    /**
     * 1
     * @param command 1
     * @param args 1
     * @return 1
     */
    public boolean runCommand(Command command, Object... args) {
        if(isParentAttached()) return getParent().runKeyedCommand(getParentKey(), command, args);
        return false;
    }


    //----------IMPLEMENT Runnable----------//
    @Override
    public void run(){
        while(!queuedGroupActions.isEmpty()) queuedGroupActions.removeFirst().run();

        activeRunnables.forEach((k,v) -> v.run());
    }


    //----------INFO----------//

    /**
     * 1
     * @param tab 1
     * @param start 1
     * @return 1
     */
    protected StringBuilder getBaseInfo(String tab, String start){
        StringBuilder str = new StringBuilder(start);
        str.append(getName() + " Info:");
        str.append("\n");
        str.append(start + tab + "Type: " + getClass().getSimpleName());
        str.append("\n");
        str.append(start + tab + "Status: ");
        if(!isParentAttached())
            str.append("No Parent");
        else if (isRunning())
            str.append("Running");
        else
            str.append("Not Running");

        return str;
    }

    private String getHashtableInfo(Map<String, Runnable> table, String tab, int startTabs, boolean extend, boolean getRunningInfo, boolean getAllInfo){
        StringBuilder str = new StringBuilder();
        String start = tab.repeat(startTabs);

        for (Map.Entry<String, Runnable> entry: table.entrySet()) {
            str.append("\n");
            str.append(start + tab + "Key: " + entry.getKey());
            str.append("\n");

            Runnable r = entry.getValue();
            if(r instanceof Task)
                str.append(((Task) r).getInfo(tab, startTabs + 2, extend));
            else if(r instanceof Group)
                str.append(((Group) r).getInfo(tab, startTabs + 2, extend, getRunningInfo, true));
            else
                str.append(start + tab.repeat(2) + r.toString());
        }

        return str.toString();
    }

    /**
     * 1
     * @param tab 1
     * @param startTabs 1
     * @param extend 1
     * @param getRunningInfo 1
     * @param getAllInfo 1
     * @return 1
     */
    public String getInfo(String tab, int startTabs, boolean extend, boolean getRunningInfo, boolean getAllInfo){
        String start = tab.repeat(startTabs);
        StringBuilder str = getBaseInfo(tab, start);


        str.append("\n");
        str.append(start + tab + "All: " + getChildrenAndKeys().size());
        if(getAllInfo)
            str.append(getHashtableInfo(getChildrenAndKeys(), tab, startTabs + 1, extend, getRunningInfo, true));

        str.append("\n");
        str.append(start + tab + "Active: " + activeRunnables.size());
        if(getRunningInfo)
            str.append(getHashtableInfo(activeRunnables, tab, startTabs + 1, extend, true, getAllInfo));

        return str.toString();
    }

    @Override
    public String toString() {
        return getInfo("│\t", 0, false, true, true);
    }

    //----------Other----------//

    /**
     * 1
     */
    public enum Command{
        NONE,
        START,
        PAUSE,
        QUE_PAUSE,
        QUE_START,
    }
}
