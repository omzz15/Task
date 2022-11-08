import om.self.task.core.EventManager;
import om.self.task.core.Group;
import om.self.task.core.OrderedEventManager;
import om.self.task.other.IncrementedTask;

import java.util.LinkedList;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        EventManager em1 = new OrderedEventManager("main");
        em1.attachToEvent(EventManager.CommonTrigger.START, "2 start", () -> System.out.println("2 started"));
        em1.attachToEvent(EventManager.CommonTrigger.START, "3 start", () -> System.out.println("3 started"));
        em1.attachToEvent(EventManager.CommonTrigger.START, "4 start", () -> System.out.println("4 started"));
        em1.attachToEvent(EventManager.CommonTrigger.START, "1 start", () -> System.out.println("1 started"));
        em1.attachToEvent(EventManager.CommonTrigger.START, "5 start", () -> System.out.println("5 started"));



        em1.triggerEvent(EventManager.CommonTrigger.START);


//        Group g1 = new Group("g");
//        g1.setMaxActiveRunnables(1);
//
//        IncrementedTask t = new IncrementedTask("t", g1);
//        t.addIncrementedStep(() -> System.out.println("test " + t.getI()), 10);
//
//        IncrementedTask t2 = new IncrementedTask("t2");
//        t2.autoStart = false;
//        t2.attachParent(g1);
//        t2.addIncrementedStep(() -> System.out.println("2 test " + t2.getI()), 10);
//
//        System.out.println(g1);
//
//        for (int i = 0; i < 5; i++) {
//            g1.run();
//        }
//
//        System.out.println(g1.runKeyedCommand("t2", Group.Command.START, true));
//
//        while (!g1.isDone())
//            g1.run();
//
//        System.out.println(g1);
    }
}