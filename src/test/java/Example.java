import om.self.task.core.EventManager;
import om.self.task.core.Group;
import om.self.task.core.OrderedGroup;
import om.self.task.core.Task;
import om.self.task.other.TimedTask;

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
//        em5.attachToEvent("e2", "r1", () -> {});
//
//        System.out.println(em1);


        OrderedGroup g1 = new OrderedGroup("main");

        g1.attachChild("t1", () -> System.out.println("task 1"));
        g1.attachChild("t2", () -> System.out.println("task 2"));
        g1.attachChild("t3", () -> System.out.println("task 3"));
        g1.attachChild("t4", () -> System.out.println("task 4"));

        g1.runKeyedCommand("t1", Group.Command.START);
        g1.runKeyedCommand("t3", Group.Command.START);
        g1.runKeyedCommand("t4", Group.Command.START);
        g1.runKeyedCommand("t2", Group.Command.START, 0);

        g1.run();
    }
}