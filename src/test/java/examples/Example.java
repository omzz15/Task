package examples;

import om.self.task.event.EventManager;
import om.self.task.event.OrderedEventManager;

/**
 * A simple example with a command and an incremented task that interrupts the command
 */
public class Example {
    public static void main(String[] args) {
        EventManager em = new OrderedEventManager("main");

        final String TEST = "TEST2";

        em.attachToEvent(TEST, "info", () -> System.out.println("event triggered!"));
        em.attachToEvent(TEST, "info 2", () -> System.out.println("event triggered 2!"));
        em.attachToEvent(TEST, "info", () -> System.out.println("event triggered 3!"));
        em.attachToEvent("TEST", "info", () -> em.clearEvent("TEST2"));

//        em.triggerEvent("TEST");
        em.triggerEvent(TEST);
    }
}