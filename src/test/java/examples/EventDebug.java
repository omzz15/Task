package examples;

import om.self.task.event.EventManager;

public class EventDebug {
    public static void main(String[] args) {
        //----------Event Manager Exception----------//
        //create 2 event managers to test propagation of exceptions
        EventManager mainEventManager = new EventManager("main");
        EventManager subEventManager = new EventManager("sub", mainEventManager);

        //create a task that randomly throws an exception
        subEventManager.attachToEvent("test", "throw exception", () -> {
            throw new RuntimeException("Can you catch me?");
        });

        //trigger the event
        mainEventManager.triggerEvent("sub/test");

        //the exception will look like this:
        //Exception in thread "main" java.lang.RuntimeException: Error while running 'throw exception' in event 'test' at directory 'main/sub'
        //    at om.self.task.event.EventManager.lambda$triggerEvent$4(EventManager.java:252)
        //    at java.base/java.util.Hashtable.forEach(Hashtable.java:895)
        //    at om.self.task.event.EventManager.triggerEvent(EventManager.java:248)
        //    at examples.EventDebug.main(EventDebug.java:18)
        //Caused by: java.lang.RuntimeException: Can you catch me?
        //    at examples.EventDebug.lambda$main$0(EventDebug.java:14)
        //    at om.self.task.event.EventManager.lambda$triggerEvent$4(EventManager.java:250)
        //    ... 3 more
    }
}
