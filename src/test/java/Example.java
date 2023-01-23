import om.self.task.core.*;
import om.self.task.event.EventContainer;
import om.self.task.event.EventManager;
import om.self.task.event.WaitForAllEvents;
import om.self.task.other.IncrementedTask;
import om.self.task.other.TimedTask;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Example {
    public static void main(String[] args) {
//        Group g1 = new Group("g1");
//
//        g1.attachChild("r1", () -> {});
//        g1.attachChild("r2", () -> {});
//        new Task("t1", g1);
//        new TimedTask("tt1", g1);
//
//        Group g2 = new Group("g2", g1);
//
//        g2.attachChild("r1", () -> {});
//        g2.attachChild("r2", () -> {});
//        new Task("t1", g2);
//        new TimedTask("tt1", g2);
//
//        Group g3 = new Group("g3", g1);
//
//        g3.attachChild("r1", () -> {});
//        g3.attachChild("r2", () -> {});
//        new Task("t1", g3);
//        new TimedTask("tt1", g3);
//
//        Group g4 = new Group("g4", g2);
//
//        g4.attachChild("r1", () -> {});
//        g4.attachChild("r2", () -> {});
//        new Task("t1", g4);
//        new TimedTask("tt1", g4).addDelay(1000);
//
//        //g4.runCommand(Group.Command.START);
//        g1.run();
//        g1.run();
//
//        System.out.println(g1.getInfo("│\t", 0));


//        EventManager em1 = new EventManager("m1");
//        em1.attachToEvent("e1", "r1", () -> {});
//        em1.attachToEvent("e1", "r2", () -> {});
//        em1.attachToEvent("e2", "r1", () -> {});
//
//        EventManager em2 = new EventManager("m2", em1);
//        em2.attachToEvent("e1", "r1", () -> {});
//        em2.attachToEvent("e1", "r2", () -> {});
//        em2.attachToEvent("e2", "r1", () -> {});
//
//        EventManager em3 = new EventManager("m3", em1);
//        em3.attachToEvent("e1", "r1", () -> {});
//        em3.attachToEvent("e1", "r2", () -> {});
//        em3.attachToEvent("e2", "r1", () -> {});
//
//        EventManager em4 = new EventManager("m4", em3);
//        em4.attachToEvent("e1", "r1", () -> {});
//        em4.attachToEvent("e1", "r2", () -> {});
//        em4.attachToEvent("e2", "r1", () -> {});
//
//        EventManager em5 = new EventManager("m5", em2);
//        em5.attachToEvent("e1", "r1", () -> {});
//        em5.attachToEvent("e1", "r2", () -> {});
//        em5.attachToEvent("e2", "r1", () -> System.out.println("e2 in e5 triggered"));
//
//        System.out.println(em1);
//
//        em1.triggerEvent("m2/m5/e2");


//        Group main = new Group("main");
//        GroupEx sub = new GroupEx("sub", main);
//        new Task("test", sub).setRunnable(() -> System.out.println("hello from sub"));
//
//
//        TaskEx t = new TaskEx("test", main);
//        EventManager em = new EventManager("main");
//
//        sub.waitForEvent("trigger", em);//, () -> System.out.println("sub parked"));
//
//
//        t.addStep(() -> System.out.println("pre event"));
//        t.waitForEvent("trigger", em);
//        t.addStep(() -> System.out.println("waiting..."));
//        t.waitForEvent("trigger", em, () -> System.out.println("waiting"));
//        t.addStep(() -> System.out.println("post event"));
//
//        System.out.println(main);
//        main.run();
//        main.run();
//        System.out.println(em);
//        main.run();
//        System.out.println(main);
//
//        while(!main.isDone()){
//            main.run();
//            if(Math.random() < 0.05)
//            {
//                System.out.println(em);
//                em.triggerEvent("trigger");
//                main.run();
//                break;
//            }
//        }


//        Group g = new OrderedGroup("main");
//        Task t = new Task("test", g);
//        t.setRunnable(() -> System.out.println("hello!"));
//        IncrementedTask it = new IncrementedTask("it", g);
//        it.addIncrementedStep(() -> System.out.println("waiting"), 10);
//        it.addStep(() -> g.runKeyedCommand("test", Group.Command.PAUSE));
//        it.addIncrementedStep(() -> System.out.println("paused"), 10);
//
//        while (!g.isDone())
//            g.run();

//        CopyOnWriteArrayList<Runnable> test = new CopyOnWriteArrayList<>();
//
//        test.add(() -> System.out.println("first"));
//        test.add(() -> test.remove(0));
//        test.add(() -> System.out.println("second"));
//        test.add(() -> System.out.println("second2"));
//
//        for(Runnable r : test)
//            r.run();
//
//        for(Runnable r : test)
//            r.run();

//        TimedTask test = new TimedTask("test");
//
//        test.addStep(() -> System.out.println("hello 1"));
//        test.addStep(() -> System.out.println("hello 2"));
//        test.addDelay(1000);
//        test.addStep(() -> System.out.println("hello 3"));
//
//        while (!test.isDone()){
//            test.run();
//        }
//
//        System.out.println("complete");


//        EventManager em = new EventManager("main");
//        //EventContainer ec = new EventContainer(em, "t3");
//        WaitForAllEvents test = new WaitForAllEvents("test", new EventContainer(em, "out"), new EventContainer(em, "t1"),new EventContainer(em, "t2"), new EventContainer(em, "t3"));
//
//        em.attachToEvent("out", "print", () -> System.out.println("all triggered"));
//
//        System.out.println(em);
//
//        em.triggerEvent("t1");
//        em.triggerEvent("t2");
//        //em.triggerEvent("t3");
//        test.detachEvents(new EventContainer(em, "t3"));
//
//        Group g = new Group("1");
//        IncrementedTask t = new IncrementedTask("2");
//        IncrementedTask t2 = new IncrementedTask("3");


        EventManager em = new EventManager("main");
        Group g = new Group("main");
        TaskEx tx = new TaskEx("test", g);

        tx.addStep(() -> System.out.println("waiting..."));
        tx.waitForEvent(em.getContainer("wait"));
        tx.addStep(() -> System.out.println("done waiting!"));
        tx.addStep(() -> System.out.println("done waiting! 2"));

//        for(int i = 0; i < 30; i++){
//            g.run();
//            if(i == 1)
//                tx.runCommand(Group.Command.PAUSE);
//            if(i == 20)
//                tx.runCommand(Group.Command.START);
//            System.out.println(i);
//        }

        int loops = 0;
        while (!g.isDone()){
            g.run();
            System.out.println(loops);
            if(loops > 10)
                em.triggerEvent("wait");
            loops++;
        }
//
//        System.out.println(loops);

//        Group main = new Group("main");
//        TimedTask killer = new TimedTask("killer", main);
//        TaskEx tx = new TaskEx("test", main);
//
//        tx.addStep(() -> {
//            System.out.println("hello 1");
//            tx.addNextStep(() -> System.out.println("hello 1.5"), () -> true);
//        });
//
//        tx.addStep(() -> System.out.println("hello 2"));
//
//        killer.addDelay(1000);
//        killer.addStep(() -> tx.runCommand(Group.Command.PAUSE));
//        //killer.addStep(() -> System.out.println("killed!"));
//
//        long start = System.currentTimeMillis();
//
//        while (!main.isDone()){
//            main.run();
//        }
//
//        System.out.println("time: " + (System.currentTimeMillis() - start));
    }
}