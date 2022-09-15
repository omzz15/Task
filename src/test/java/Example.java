import om.self.task.core.Group;
import om.self.task.other.IncrementedTask;

public class Example {
//    public static void main(String[] args){
//        Group main = new Group("main");
//        for(int i = 0; i < 10; i++){
//            int type = rand3();
//            if(type == 0)
//                new Task("Task " + i, main);
//            else if(type == 1)
//                new TaskEx("TaskEx " + i, main);
//            else
//                main.attachChild("Runnable " + i, () -> {});
//        }
//
//        Group part1 = new Group("part 1", main);
//        for(int i = 0; i < 10; i++){
//            int type = rand3();
//            if(type == 0)
//                new Task("Task " + i, part1);
//            else if(type == 1)
//                new TaskEx("TaskEx " + i, part1);
//            else
//                part1.attachChild("Runnable " + i, () -> {});
//        }
//
//        Group part2 = new Group("part 2", main);
//        for(int i = 0; i < 10; i++){
//            int type = rand3();
//            if(type == 0)
//                new Task("Task " + i, part2);
//            else if(type == 1)
//                new TaskEx("TaskEx " + i, part2);
//            else
//                part2.attachChild("Runnable " + i, () -> {});
//        }
//
//        Group part3 = new Group("part 3", main);
//        for(int i = 0; i < 10; i++){
//            int type = rand3();
//            if(type == 0) {
//                Task t = new Task("Task " + i, part3);
//                t.setRunnable(() -> {
//                    System.out.println("hello");
//                });
//            }
//            else if(type == 1)
//                new TaskEx("TaskEx " + i, part3);
//            else
//                part3.attachChild("Runnable " + i, () -> {});
//        }
//
//        System.out.println(main);
//        while (!main.isDone())
//            main.run();
//    }
//
//    static int rand3(){
//        return (int)(Math.random() * 3);
//    }

    public static void main(String[] args) {
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