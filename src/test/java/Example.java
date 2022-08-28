import om.self.task.core.Group;
import om.self.task.core.Task;
import om.self.task.core.TaskEx;
import om.self.task.other.DelayTask;
import om.self.task.other.IncrementedTask;
import om.self.task.other.Que;

public class Example {
    public static void main(String[] args){
        //Group main = new Group("main");

        TaskEx t = new Que("test");

        DelayTask delay = new DelayTask("");
        //delay.setAutoReset(false);
        delay.addDelay(100);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                int finalI = i;
                int finalJ = j;
                t.addStep(() -> System.out.print("\rStep " + finalI + " is running" + ".".repeat(finalJ)));
                t.addStep(delay);
                //t.addStep(() -> delay.reset());
            }
        }

        while (!t.isDone()) {
            t.run();
        }
    }
}