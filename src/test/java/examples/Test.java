package examples;

import om.self.task.core.Group;
import om.self.task.core.TaskEx;
import om.self.task.event.EventManager;
import om.self.task.other.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Test {

    private static String getDir(){
        return "robot/drive";
    }
    public static void main(String[] args) {
        Group main = new Group("main");
        Group taskManager = new Group("task manager", main);
        taskManager.attachChild("test 1", () -> {});
        new TaskEx("test 2", taskManager).addStep(() -> {}, () -> false);

//        taskManager.runCommand(Group.Command.START, new AbstractMap.SimpleEntry<>(Group.CommandVars.startWhenDone, false));
        System.out.println(main);
    }

}