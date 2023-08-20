package om.self.task.core;

import om.self.structure.NamedStructure;
import om.self.structure.bidirectional.KeyedBidirectionalStructure;
import om.self.task.event.EventManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static om.self.task.other.Utils.repeat;

/**
 * This class stores and manages (running when needed) a group of child runnables (which can include other groups as well).
 * It also acts as a structural class that can be attached to a parent group to create complex and highly flexible structures of groups.
 */
public class Group extends KeyedBidirectionalStructure<String, Group, Runnable> implements Runnable, NamedStructure<String>{
    /**
     * These are common variables used in {@link #runCommand(Command, Map.Entry[])}
     */
    public static final class CommandVars{
        /**
         * Used to force start a runnable even if the max active runnable is reached by stopping a different one
         */
        public static final String forceActiveRunnable = "force active runnable";
        /**
         * Used in {@link OrderedGroup} to specify an index for the runnable to be inserted at
         */
        public static final String location = "location";
        /**
         * Used in {@link OrderedGroup} to specify whether the runnable can be added multiple times
         */
        public static final String allowMultiRun = "allow multi run";
        /**
         * Used in {@link om.self.task.core.Command} to tell weather the command was interrupted or stopped on its own
         */
        public static final String interrupted = "interrupted";

        /**
         * Used in {@link Group} to tell weather to start a group even if {@link #isDone()} returns true(meaning the group is not waiting and there are no active or queued things). If true, the group will always start even if the group is done. If false, it will only start if {@link #isDone()} returns false.
         */
        public static final String startWhenDone = "start when done";
    }

    /**
     * The name of the group (used for identification and when printing a tree of groups)
     */
    private String name;
    /**
     * Stores all the active runnables in this group
     */
    protected final ConcurrentHashMap<String, Runnable> activeRunnables = new ConcurrentHashMap<>();
    /**
     * Stores actions that need to be run the next time {@link #run()} is called
     */
    protected final LinkedList<Runnable> queuedGroupActions = new LinkedList<>();

    /**
     * Whether this group should automatically be started
     */
    public AutoManagePolicy autoStartPolicy = AutoManagePolicy.ONLY_WHEN_EMPTY;
    /**
     * When this group should automatically be stopped
     */
    public AutoManagePolicy autoStopPolicy = AutoManagePolicy.ONLY_WHEN_EMPTY;

    /**
     * The number of maximum active runnables at a time(-1 means infinity)
     */
    private int maxActiveRunnables = -1;

    /**
     * If {@link CommandVars#forceActiveRunnable} is not supplied to {@link #runCommand(Command, Map.Entry[])} then this will be used as the default value
     */
    public boolean forceActiveRunnablesDefault = false;

    /**
     * Tells whether this group is waiting for a runnable to finish (used when a runnable is waiting for an event) <br>
     * If this is true, {@link Group#isDone()}
     * can not be true
     * but this will not affect auto pause so the group is free to pause if there are no other active runnable
     */
    private boolean waiting = false;

//    /**
//     * The character used
//     * to separate the groups when using things like {@link Group#runKeyedCommand(String, Command, Map.Entry[])}.
//     */
//    public static String dirChar = "/";

    //----------CONSTRUCTOR----------//
    /**
     * Basic constructor that just sets the name of this group without attaching parent
     * @param name the name of this group
     */
    public Group(String name){
        this(name, null, null);
    }

    /**
     * Constructor that sets the name of this group then attaches it to a parent with the parent key as name
     * @param name the name of this group and the key used to identify this to parent
     * @param parent the parent this group is attached to
     */
    public Group(String name, Group parent){
        this(name, name, parent);
    }

    /**
     * Constructor that sets the name of this group then attaches it to a parent with the parent key as parentKey
     * @param name the name of this group
     * @param parentKey the key used to identify this to parent
     * @param parent the parent this group is attached to
     */
    public Group(String name, String parentKey, Group parent){
        this.name = name;
        if(parent != null) {
            attachParent(parentKey, parent);
        }
    }

    //----------GETTER and SETTER----------//

    /**
     * gets the name of this group
     * @return {@link Group#name}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * sets the name of this group
     * @param name the new name of this group
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets all active(running) runnables <br>
     * IMPORTANT: DO NOT use this to add or remove from the active runnables.
     * Use {@link Group#runKeyedCommand(String, Command, Map.Entry[])} because there are checks that need to be run.
     * @return {@link Group#activeRunnables}
     */
    public ConcurrentHashMap<String, Runnable> getActiveRunnables() {
        return activeRunnables;
    }

    /**
     * Gets a specific active runnable. If nothing is stored with the key, then it returns null.
     * @param key the key associated with runnable you want
     * @return the active runnable associated with the passed in key
     */
    public Runnable getActiveRunnable(String key){
        return activeRunnables.get(key);
    }

    /**
     * gets the queued actions to run whenever {@link #run()} is called
     * @return {@link #queuedGroupActions}
     */
    public LinkedList<Runnable> getQueuedGroupActions() {
        return queuedGroupActions;
    }

    /**
     * adds a runnable to {@link #queuedGroupActions} so it can be run the next time {@link #run()} is called
     * @param runnable the runnable to add
     */
    public void addToQueuedGroupActions(Runnable runnable){
        queuedGroupActions.add(runnable);
    }

    /**
     * gets the maximum number of runnables that can run at once (anything less than one is infinity)
     * @return {@link #maxActiveRunnables}
     */
    public int getMaxActiveRunnables() {
        return maxActiveRunnables;
    }

    /**
     * sets the maximum number of runnables that can run at once (anything less than one is infinity)
     * @param maxActiveRunnables the maximum active runnables
     */
    public void setMaxActiveRunnables(int maxActiveRunnables) {
        if(maxActiveRunnables == 0) maxActiveRunnables = -1;
        this.maxActiveRunnables = maxActiveRunnables;
    }

    /**
     * Returns if this group is waiting for a runnable to finish (used when a runnable is waiting for an event)
     * @return {@link #waiting}
     */
    public boolean isWaiting() {
        return waiting;
    }

    /**
     * Sets a flag that tells this that it is still waiting for something so {@link Group#isDone()} can not be true. This will not affect auto pause so the group is free to pause if there are no other active runnable
     * @param waiting if this group is waiting for something
     */
    public void setWaiting(boolean waiting) {
        setWaiting(waiting, true);
    }

    /**
     * Sets the {@link #waiting} flag for this group and parent groups if propagate is true.
     * @param waiting if this group is waiting for something (used when a runnable is waiting for an event)
     * @param propagate if this should propagate to parent groups
     */
    public void setWaiting(boolean waiting, boolean propagate) {
        this.waiting = waiting;
        if(isParentAttached() && propagate)
            getParent().setWaiting(waiting);
    }

    //----------CHECKS----------//
    /**
     * Checks if this groups is in the active runnables of its parent <br>
     * Note: This method only works if a parent is attached.
     * @return if this group is running
     */
    public boolean isRunning() {
        return isParentAttached() && getParent().getActiveRunnable(getParentKey()) != null;
    }

    /**
     * Checks if this group has any active runnables and also if it is waiting for anything
     * @return if this group is done
     */
    public boolean isDone(){
        return activeRunnables.isEmpty() && queuedGroupActions.isEmpty() && !waiting;
    }

    /**
     * Returns if there is a child in active runnables with the passed in key (meaning it is running)
     * @param key the key associated with the child
     * @return if the child is running
     */
    public boolean isChildRunning(String key){
        return getActiveRunnable(key) != null;
    }


    //----------IMPLEMENT Structure methods----------//


//    @Override
//    public Runnable getChild(String key) {
//
//        List<String> l = Arrays.stream(key.split(dirChar)).collect(Collectors.toList());
//        getChild(l.remove(0)).getChild(String.join(dirChar, l));
//
//        return super.getChild(key);
//    }

    /**
     * Used to remove the child from active runnables before it is detached <br>
     * IMPORTANT: DO NOT use this method, it is meant to be internal
     * @param key the key associated with the child
     * @param child the child to detach
     */
    @Override
    public void onChildDetach(String key, Runnable child) {
        removeFromActive(key);
    }

    /**
     * This will clear all the children (runnables) attached to this group
     */
    public void clear(){
        for (String s: getChildKeys()) detachChild(s);
    }

    /**
     * This will attach a parent to this group
     * @param group the parent group
     */
    public void attachParent(Group group) {
        attachParent(name, group);
    }

    /**
     * This will attach a child to this group
     * @param child the child to attach
     */
    public void attachChild(NamedStructure<String> child){
        attachChild(child.getName(), (Runnable) child);
    }

    //----------Commands----------//
    /**
     * This will run a command on the child with the passed in key <br>
     * If you want to run a command on the parent of this group, use {@link #runCommand(Command, Map.Entry[])}
     * @param key the key associated with the child
     * @param command the command to run
     * @param args the arguments to pass to the command
     * @return if the command was run successfully
     */
    public boolean runKeyedCommand(String key, Command command, Map.Entry<String, Object>... args) {
        Runnable child = getChild(key);
        if (child instanceof Task){
            if (((Task) child).lockState)
                return false;
        }
        else if(child instanceof Group)
            if (command == Command.START && !getArg(CommandVars.startWhenDone, true, args) && ((Group) child).isDone())
                return false;

        switch (command){
            case START: {
                if (activeRunnables.size() == maxActiveRunnables) {
                    boolean force = getArg(CommandVars.forceActiveRunnable, forceActiveRunnablesDefault, args);
                    if (!force) return false;

                    removeFromActive(activeRunnables.keys().nextElement());
                }
                return startRunnable(key, args);
            }
            case PAUSE: {
                removeFromActive(key, args);
                if (isParentAttached() && (autoStopPolicy == AutoManagePolicy.ALWAYS || (autoStopPolicy == AutoManagePolicy.ONLY_WHEN_EMPTY && activeRunnables.isEmpty())))
                    return runCommand(Command.PAUSE);
                return true;
            }
            case NONE:
                return true;
            default:
                return false;
        }
    }

    /**
     * This will get an argument with the specified name from the passed in arguments
     * @param name the name of the argument
     * @param args the arguments to search through
     * @return an optional with the argument value or an empty optional if the argument was not found
     */
    protected Optional<Object> getArg(String name, Map.Entry<String, Object>... args){
        for (Map.Entry<String, Object> arg: args){
            if(Objects.equals(arg.getKey(), name)) return Optional.of(arg.getValue());
        }
        return Optional.empty();
    }

    /**
     * This will get an argument with the specified name from the passed in arguments or return the default value if the argument was not found
     * @param name the name of the argument
     * @param defaultVal the default value to return if the argument was not found
     * @param args the arguments to search through
     * @return the argument value or the default value if the argument was not found
     * @param <T> the type of the argument and default value
     */
    protected<T> T getArg(String name, T defaultVal, Map.Entry<String, Object>... args){
        for (Map.Entry<String, Object> arg: args){
            if(Objects.equals(arg.getKey(), name)) return (T)(arg.getValue());
        }
        return defaultVal;
    }

    /**
     * This will add a runnable to the active runnables
     * @param key the key associated with the runnable
     * @param runnable the runnable to add
     * @param args the arguments to pass to the runnable
     */
    protected void addToActive(String key, Runnable runnable, Map.Entry<String, Object>... args){
        if(activeRunnables.put(key, runnable) == runnable) //make sure this actually started something
            return;

        if(runnable instanceof om.self.task.core.Command)
            ((om.self.task.core.Command)runnable).start();
    }

    /**
     * This will remove a runnable from the active runnables
     * @param key the key associated with the runnable
     * @param args the arguments to pass to the runnable
     */
    protected void removeFromActive(String key, Map.Entry<String, Object>... args){
        Runnable runnable = activeRunnables.remove(key);

        if(runnable == null) //make sure this actually stopped something
            return;

        if(runnable instanceof om.self.task.core.Command){
            boolean interrupted = getArg(CommandVars.interrupted, true, args);
            ((om.self.task.core.Command)runnable).stop(interrupted);
        }

    }

    /**
     * This will start a runnable with the specified key
     * @param key the key associated with the runnable
     * @param args the arguments to pass to the runnable
     * @return if the runnable was started
     */
    private boolean startRunnable(String key, Map.Entry<String, Object>... args){
        Runnable runnable = getChild(key);
        if(runnable == null) return false;
        addToActive(key, runnable, args);
        if(isParentAttached() && !isRunning())
            if(autoStartPolicy == AutoManagePolicy.ALWAYS || (autoStartPolicy == AutoManagePolicy.ONLY_WHEN_EMPTY && activeRunnables.size() == 1))
                runCommand(Command.START, args);
        return true;
    }

    /**
     * This will run a command on this group from the parent of this group
     * (this calls {@link #runKeyedCommand(String, Command, Map.Entry[])} with the key of this group) <br>
     * Note: to use this a parent must be attached
     * @param command the command to run
     * @param args the arguments to pass to the command
     * @return if the command was run successfully
     */
    public boolean runCommand(Command command, Map.Entry<String, Object>... args) {
        if(isParentAttached()) return getParent().runKeyedCommand(getParentKey(), command, args);
        return false;
    }

    /**
     * This will cause the group to wait for the specified event before starting
     * @param event the name of the event to wait for
     * @param manager the event manager to attach to
     */
    public void waitForEvent(String event, EventManager manager){
        setWaiting(true);
        manager.singleTimeAttachToEvent(event, "start group - " + getName(), () -> {
            setWaiting(false);
            runCommand(Group.Command.START);
        });
        runCommand(Command.PAUSE);
    }

    //----------IMPLEMENT Runnable----------//

    /**
     * This will run all queued actions (run {@link #queuedGroupActions})
     */
    protected void runQueuedActions(){
        while(!queuedGroupActions.isEmpty()) queuedGroupActions.removeFirst().run();
    }

    /**
     * This will run any queued actions and then run all active runnables <br>
     * Note:
     * This is automatically called by a patent group if attached
     * so this really only needs to be called if this is a root group
     */
    @Override
    public void run(){
        runQueuedActions();
        activeRunnables.forEach((k, v) -> {
            try{
                v.run();
            } catch (Exception e) {
                throw new RuntimeException("Error running '" + k + "' in group '" + getName() + "'", e);
            }
        });
    }




    //----------INFO----------//

    /**
     * This will get the info for this group
     * @param tab the tab style to use
     * @param start the string to start with
     * @return the info for this group as a string
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

    /**
     * This will get the info for this group and all of its children
     * @param table the table to get the info from
     * @param tab the tab style to use
     * @param startTabs the number of tabs to start with
     * @param extend if the info should be extended
     * @param getRunningInfo if the running info should be included
     * @param getAllInfo if all info should be included (currently not used)
     * @return the info for this group and all of its children as a string
     */
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
     * This will get the info for this group and all of its children
     * @param tab the tab style to use
     * @param startTabs the number of tabs to start with
     * @param extend if the info should be extended
     * @param getRunningInfo if the running info should be included
     * @param getAllInfo if all info should be included (currently not used)
     * @return the info for this group and all of its children as a string
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

    /**
     * This will get the info for this group and all of its children
     * @param tab the tab style to use
     * @param startTabs the number of tabs to start with
     * @return the info for this group and all of its children as a string
     */
    public String getInfo(String tab, int startTabs){
        return getInfo(repeat(tab, startTabs), tab);
    }

    /**
     * This will get the info for this group and all of its children
     * @param start the string to start with
     * @param tab the tab style to use
     * @return the info for this group and all of its children as a string
     */
    public String getInfo(String start, String tab){
        return start + "All:\n" +
                start + tab + getName() + ": " + getClass().getSimpleName() + "(current parent)\n" +
                getInfoRecursively(start + repeat(tab, 2), tab, g -> g.getChildrenAndKeys().entrySet()) +
                "\n" + start + "Active:\n" +
                start + tab + getName() + ": " + getClass().getSimpleName() + "(current parent)\n" +
                getInfoRecursively(start + repeat(tab, 2), tab, g -> g.activeRunnables.entrySet());
    }

    /**
     * This will get the info for this group and all of its children recursively
     * @param start the string to start with
     * @param tab the tab style to use
     * @param func ??
     * @return the info for this group and all of its children recursively as a string
     */
    private String getInfoRecursively(String start, String tab, Function<Group, Set<Map.Entry<String, Runnable>>> func){
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Runnable> entry : func.apply(this)) {
            str.append(start).append(entry.getKey()).append(": ").append(entry.getValue().getClass().getSimpleName()).append("\n");
            if(entry.getValue() instanceof Group)
                str.append(((Group) entry.getValue()).getInfoRecursively(start + tab, tab, func));
        }
        return str.toString();
    }

    /**
     * This will get the info for this group and all of its children
     * @return the info for this group and all of its children as a string
     */
    @Override
    public String toString() {
        return getInfo("","│\t");
    }

    //----------Other----------//

    /**
     * These are commands that can be run on Groups and their children
     */
    public enum Command{
        /**
         * Run nothing
         */
        NONE,
        /**
         * start the runnable that this command is run on
         */
        START,
        /**
         * stop the runnable that this command is run on
         */
        PAUSE,
    }

    /**
     * These are the different policies that can be used for auto managing groups
     */
    public enum AutoManagePolicy{
        /**
         * This will disable auto managing for this group
         */
        DISABLED,
        /**
         * This will only auto manage this group when it is empty
         */
        ONLY_WHEN_EMPTY,
        /**
         * This will always auto manage this group
         */
        ALWAYS
    }
}