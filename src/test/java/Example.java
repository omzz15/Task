import om.self.task.core.EventManager;
import om.self.task.core.Group;
import om.self.task.other.IncrementedTask;

import java.util.LinkedList;
import java.util.List;

public class Example {
    public static void main(String[] args) {
//        EventManager em1 = new EventManager("main");
//        em1.attachToEvent(EventManager.CommonTrigger.START, "main start", () -> System.out.println("main started"));
//
//        EventManager em2 = new EventManager("robot", em1);
//        em2.attachToEvent(EventManager.CommonTrigger.START, "robot start", () -> System.out.println("robot started"));
//
//        EventManager em3 = new EventManager("drive", em2);
//        em3.attachToEvent(EventManager.CommonTrigger.START, "drive start", () -> System.out.println("drive started"));
//
//        System.out.println(em3.getDir());


        Group g1 = new Group("g");
        g1.setMaxActiveRunnables(1);

        IncrementedTask t = new IncrementedTask("t", g1);
        t.addIncrementedStep(() -> System.out.println("test " + t.getI()), 10);

        IncrementedTask t2 = new IncrementedTask("t2");
        t2.autoStart = false;
        t2.attachParent(g1);
        t2.addIncrementedStep(() -> System.out.println("2 test " + t2.getI()), 10);

        System.out.println(g1);

        for (int i = 0; i < 5; i++) {
            g1.run();
        }

        System.out.println(g1.runKeyedCommand("t2", Group.Command.START, true));

        while (!g1.isDone())
            g1.run();

        System.out.println(g1);
    }
}