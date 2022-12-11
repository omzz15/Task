package om.self.task.core;

import om.self.structure.NamedStructure;
import om.self.structure.bidirectional.KeyedBidirectionalStructure;

import java.util.*;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * A structure class that can manage and run {@link Runnable} like {@link Task}.
 */
public class Group extends KeyedBidirectionalStructure<String, Group, Runnable> implements Runnable, NamedStructure<String>{
    public static final class CommandVars{
        public static final String forceActiveRunnable = "force active runnable";
        public static final String location = "location";
        public static final String allowMultiRun = "allow multi run";
    }

    private String name;
    protected final Hashtable<String, Runnable> activeRunnables = new Hashtable<>();
    protected final LinkedList<Runnable> queuedGroupActions = new LinkedList<>();

    /**
     * Whether this group should automatically be paused and started based on if there are any active runnables
     */
    public AutoManagePolicy autoStartPolicy = AutoManagePolicy.ONLY_WHEN_EMPTY;
    public AutoManagePolicy autoStopPolicy = AutoManagePolicy.ONLY_WHEN_EMPTY;

    /**
     * the number of maximum active runnables at a time(-1 means infinity)
     */
    private int maxActiveRunnables = -1;

    /**
     * 1
     */
    public boolean forceActiveRunnablesDefault = false;

    private boolean waiting = false;

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
     * @apiNote DO NOT use this to add or remove from the active runnables. Use {@link Group#runKeyedCommand(String, Command, Map.Entry[])} because there are checks that need to be run.
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

    public boolean isWaiting() {
        return waiting;
    }

    /**
     * a flag that tells this that it is still waiting for something so {@link Group#isDone()} can not be true. This will not affect auto pause so the group is free to pause if there are no other active runnable
     * @param waiting if this group is waiting for something
     */
    public void setWaiting(boolean waiting) {
        setWaiting(waiting, true);
    }

    public void setWaiting(boolean waiting, boolean propagate) {
        this.waiting = waiting;
        if(isParentAttached() && propagate)
            getParent().setWaiting(waiting);
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
        return activeRunnables.isEmpty() && queuedGroupActions.isEmpty() && !waiting;
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
        removeFromActive(key);
    }

    public void clear(){
        for (String s: getChildKeys()) detachChild(s);
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
    public boolean runKeyedCommand(String key, Command command, Map.Entry<String, Object>... args){
        switch (command){
            case START: {
                try {
                    if (activeRunnables.size() == maxActiveRunnables) {
                        boolean force = getArg(CommandVars.forceActiveRunnable, forceActiveRunnablesDefault, args);
                        if (!force) return false;

                        removeFromActive(activeRunnables.keys().nextElement());
                    }

                    return startRunnable(key, args);
                } catch (ConcurrentModificationException ignore){
                    Runnable start = () -> runKeyedCommand(key, Command.START, args);
                    if(!queuedGroupActions.contains(start))
                        addToQueuedGroupActions(start);
                }
            }
            case PAUSE: {
                try {
                    removeFromActive(key);
                    if (autoStopPolicy != AutoManagePolicy.DISABLED && isParentAttached() && activeRunnables.isEmpty())
                        runCommand(Command.PAUSE);
                } catch (ConcurrentModificationException ignore){
                    Runnable pause = () -> runKeyedCommand(key, Command.PAUSE);
                    if(!queuedGroupActions.contains(pause))
                        addToQueuedGroupActions(pause);
                }
                break;
            }
            case NONE:
                return true;
            default:
                return false;
        }
        return true;
    }

    protected Optional<Object> getArg(String name, Map.Entry<String, Object>... args){
        for (Map.Entry<String, Object> arg: args){
            if(Objects.equals(arg.getKey(), name)) return Optional.of(arg.getValue());
        }
        return Optional.empty();
    }

    protected<T> T getArg(String name, T defaultVal, Map.Entry<String, Object>... args){
        for (Map.Entry<String, Object> arg: args){
            if(Objects.equals(arg.getKey(), name)) return (T)(arg.getValue());
        }
        return defaultVal;
    }

    protected void addToActive(String key, Runnable runnable, Map.Entry<String, Object>... args){
        activeRunnables.put(key, runnable);
    }
    protected void removeFromActive(String key, Map.Entry<String, Object>... args){
        activeRunnables.remove(key);
    }

    private boolean startRunnable(String key, Map.Entry<String, Object>... args){
        Runnable runnable = getChild(key);
        if(runnable == null) return false;
        addToActive(key, runnable, args);
        if(isParentAttached() && !isRunning())
            if(autoStartPolicy == AutoManagePolicy.ALWAYS || (autoStartPolicy == AutoManagePolicy.ONLY_WHEN_EMPTY && activeRunnables.size() == 1))
                if(getParent().isRunning()) runCommand(Command.START, args);
                else runCommand(Command.START, args);
        return true;
    }

    /**
     * 1
     * @param command 1
     * @param args 1
     * @return 1
     */
    public boolean runCommand(Command command, Map.Entry<String, Object>... args) {
        if(isParentAttached()) return getParent().runKeyedCommand(getParentKey(), command, args);
        return false;
    }

    public void waitForEvent(String event, EventManager manager){
        setWaiting(true);
        manager.singleTimeAttachToEvent(event, "start group - " + getName(), () -> {
            setWaiting(false);
            runCommand(Group.Command.START);
        });
        runCommand(Command.PAUSE);
    }

    //----------IMPLEMENT Runnable----------//
    protected void runQueuedActions(){

    }

    @Override
    public void run(){
        while(!queuedGroupActions.isEmpty()) queuedGroupActions.removeFirst().run();
        activeRunnables.forEach((k, v) -> v.run());
    }




    //----------INFO----------//

    /**
     * 1
     * @param tab 1
     * @param start 1
     * @return 1
     */
    private StringBuilder getBaseInfo(String tab, String start){
        StringBuilder str = new StringBuilder(start);
        str.append(getName()).append(" Info:")
                .append("\n")
                .append(start).append(tab).append("Type: ").append(getClass().getSimpleName())
                .append("\n")
                .append(start).append(tab).append("Status: ");
        if(!isParentAttached())
            str.append("No Parent");
        else if (isRunning())
            str.append("Running");
        else
            str.append("Not Running");

        return str;
    }



    private String getMapInfo(Map<String, Runnable> table, String tab, int startTabs, boolean extend, boolean getRunningInfo, boolean getAllInfo){
        StringBuilder str = new StringBuilder();
        String start = repeat(tab, startTabs);

        for (Map.Entry<String, Runnable> entry: table.entrySet()) {
            str.append("\n")
                    .append(start).append(tab).append("Key: ").append(entry.getKey())
            .append("\n");

            Runnable r = entry.getValue();
            if(r instanceof Task)
                str.append(((Task) r).getInfo(tab, startTabs + 2, extend));
            else if(r instanceof Group)
                str.append(((Group) r).getFullInfo(tab, startTabs + 2, extend, getRunningInfo, true));
            else
                str.append(start + repeat(tab, 2) + r.toString());
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
    public String getFullInfo(String tab, int startTabs, boolean extend, boolean getRunningInfo, boolean getAllInfo){
        String start = repeat(tab, startTabs);
        StringBuilder str = getBaseInfo(tab, start);


        str.append("\n")
                .append(start).append(tab).append("All: ").append(getChildrenAndKeys().size());
        if(getAllInfo)
            str.append(getMapInfo(getChildrenAndKeys(), tab, startTabs + 1, extend, getRunningInfo, true));

        str.append("\n").append(start).append(tab).append("Active: ").append(activeRunnables.size());
        if(getRunningInfo)
            str.append(getMapInfo(activeRunnables, tab, startTabs + 1, extend, true, getAllInfo));

        return str.toString();
    }

    public String getInfo(String tab, int startTabs){
        return getInfo(repeat(tab, startTabs), tab);
    }

    public String getInfo(String start, String tab){
        return start + start + "All:\n" +
                start + tab + getName() + ": " + getClass().getSimpleName() + "(current parent)\n" +
                getInfoRecursively(start + repeat(tab, 2), tab, g -> g.getChildrenAndKeys().entrySet()) +
                "\n" + start + "Active:\n" +
                start + tab + getName() + ": " + getClass().getSimpleName() + "(current parent)\n" +
                getInfoRecursively(start + repeat(tab, 2), tab, g -> g.activeRunnables.entrySet());
    }

    private String getInfoRecursively(String start, String tab, Function<Group, Set<Map.Entry<String, Runnable>>> func){
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Runnable> entry : func.apply(this)) {
            str.append(start).append(entry.getKey()).append(": ").append(entry.getValue().getClass().getSimpleName()).append("\n");
            if(entry.getValue() instanceof Group)
                str.append(((Group) entry.getValue()).getInfoRecursively(start + tab, tab, func));
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return getInfo("","│\t");
    }

    //----------Other----------//

    /**
     * 1
     */
    public enum Command{
        NONE,
        START,
        PAUSE,
    }

    public enum AutoManagePolicy{
        DISABLED,
        ONLY_WHEN_EMPTY,
        ALWAYS
    }
}
