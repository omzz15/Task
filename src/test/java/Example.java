import om.self.task.core.EventManager;
import om.self.task.core.Group;
import om.self.task.core.Task;
import om.self.task.other.IncrementedTask;
import org.w3c.dom.ls.LSOutput;

public class Example {
    public static void main(String[] args) {
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
        Runnable r = () -> System.out.println("start event triggered!");

        EventManager eventManager = new EventManager();

        eventManager.attachToEvent(EventManager.CommonTrigger.START, r);

        eventManager.triggerEvent(EventManager.CommonTrigger.START);
    }
}