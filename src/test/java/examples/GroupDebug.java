package examples;

import om.self.task.core.Group;
import om.self.task.core.Task;

/**
 * A simple example of how groups and event managers handle exceptions
 */
public class GroupDebug {
    public static void main(String[] args) {
        //----------Group Exception----------//
        //create 2 levels of groups to test propagation of exceptions
        Group mainGroup = new Group("mainGroup");
        Group subGroup = new Group("sub", mainGroup);

        //create a task that randomly throws an exception
        new Task("test", subGroup, () -> {
            if(Math.random() < 0.5)
                throw new RuntimeException("Can you catch me?");

            System.out.println("test is running");
        });

        //run it until an exception is thrown and see how the exception is handled
        while (!mainGroup.isDone()) {
            mainGroup.run();
        }

        //it will look like this:
        //Exception in thread "mainGroup" java.lang.RuntimeException: Error running 'subGroup' in group 'mainGroup'
        //    at om.self.task.core.Group.lambda$run$1(Group.java:433)
        //    at java.base/java.util.concurrent.ConcurrentHashMap.forEach(ConcurrentHashMap.java:1603)
        //    at om.self.task.core.Group.run(Group.java:429)
        //    at examples.DebugExample.mainGroup(DebugExample.java:19)
        //Caused by: java.lang.RuntimeException: Error running 'test' in group 'subGroup'
        //    at om.self.task.core.Group.lambda$run$1(Group.java:433)
        //    at java.base/java.util.concurrent.ConcurrentHashMap.forEach(ConcurrentHashMap.java:1603)
        //    at om.self.task.core.Group.run(Group.java:429)
        //    at om.self.task.core.Group.lambda$run$1(Group.java:431)
        //    ... 3 more
        //Caused by: java.lang.RuntimeException: Can you catch me?
        //    at examples.DebugExample.lambda$mainGroup$0(DebugExample.java:13)
        //    at om.self.task.core.Task.run(Task.java:197)
        //    at om.self.task.core.Group.lambda$run$1(Group.java:431)
        //    ... 6 more
    }
}
