import om.self.task.core.Group;
import om.self.task.core.Task;
import om.self.task.core.TaskEx;
import om.self.task.other.DelayTask;
import om.self.task.other.IncrementedTask;
import om.self.task.other.Que;

public class Example {
    public static void main(String[] args){
//        //Group main = new Group("main");
//
//        TaskEx t = new Que("test");
//
//        DelayTask delay = new DelayTask("");
//        //delay.setAutoReset(false);
//        delay.addDelay(100);
//
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 15; j++) {
//                int finalI = i;
//                int finalJ = j;
//                t.addStep(() -> System.out.print("\rStep " + finalI + " is running" + ".".repeat(finalJ)));
//                t.addStep(delay);
//                //t.addStep(() -> delay.reset());
//            }
//        }
//
//        while (!t.isDone()) {
//            t.run();
//        }

        Group main = new Group("main");
        for(int i = 0; i < 10; i++){
            int type = rand3();
            if(type == 0)
                new Task("Task " + i, main);
            else if(type == 1)
                new TaskEx("TaskEx " + i, main);
            else
                main.addRunnable("Runnable " + i, () -> {});
        }

        Group part1 = new Group("part 1", main);
        for(int i = 0; i < 10; i++){
            int type = rand3();
            if(type == 0)
                new Task("Task " + i, part1);
            else if(type == 1)
                new TaskEx("TaskEx " + i, part1);
            else
                part1.addRunnable("Runnable " + i, () -> {});
        }

        Group part2 = new Group("part 2", main);
        for(int i = 0; i < 10; i++){
            int type = rand3();
            if(type == 0)
                new Task("Task " + i, part2);
            else if(type == 1)
                new TaskEx("TaskEx " + i, part2);
            else
                part2.addRunnable("Runnable " + i, () -> {});
        }

        Group part3 = new Group("part 3", main);
        for(int i = 0; i < 10; i++){
            int type = rand3();
            if(type == 0)
                new Task("Task " + i, part3);
            else if(type == 1)
                new TaskEx("TaskEx " + i, part3);
            else
                part3.addRunnable("Runnable " + i, () -> {});
        }

        System.out.println(main);
    }

    static int rand3(){
        return (int)(Math.random() * 3);
    }
}