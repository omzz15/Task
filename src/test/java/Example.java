import om.self.task.core.Group;
import om.self.task.core.Task;
import om.self.task.other.TaskEx;

public class Example {
    public static void main(String[] args){
        Group p1 = new Group("parent 1");

        Group g1 = new Group("child 1", p1);
        //g1.setAutoManage(false);
        //g1.start();

        Group g2 = new Group("child 2", p1);
        //g2.start();

        TaskEx t1 = new TaskEx("test", g1);
        t1.addStep(()-> System.out.println("task 1 ran"));
        t1.addStep(new Task(""));
        //t1.start();

        g2.addRunnable("task 2", () -> {
            System.out.println("task 2 ran");
            t1.restart();
        });
        g2.runCommand("task 2", Group.Command.START);

        p1.run();
        p1.run();
        p1.run();
        p1.run();
        p1.run();
    }
}
