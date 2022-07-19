import om.self.logger.Logger;

public class Example{
    public static void main(String[] args) throws Exception{

        Logger.getInstance().setFile("epic.gireod", true);
        //Logger.getInstance().makeFile(false, false);

//        TaskRunner tr = new TaskRunner("runner 1");
//        int tasks = 300;
//        int maxTime = 300;
//        int maxCycles = 300;
//
//
//        for(int i = 0; i < tasks; i++){
//            TimedTask timer = new TimedTask("Timer " + i);
//            timer.addTimedStep(() -> {
//                //System.out.println(timer.getName() + " with runtime: " + timer.getCurrentRunTime());
//            }, (int)(Math.random() * maxTime));
//
//            if(Math.random() <= 0.5)
//                tr.addTask(timer, TaskRunner.Action.ADD_TO_QUE);
//            else
//                tr.addTask(timer, TaskRunner.Action.START);
//        }
//
//        for(int i = 0; i < tasks; i++){
//            IncrementedTask it = new IncrementedTask("Inctirment " + i);
//            it.addIncrementedStep(() -> {
//                //System.out.println(it.getName() + " with i: " + it.getI());
//            }, (int)(Math.random() * maxCycles));
//
//            if(Math.random() <= 0.5)
//                tr.addTask(it, TaskRunner.Action.ADD_TO_QUE);
//            else
//                tr.addTask(it, TaskRunner.Action.START);
//        }
//
//        tr.setDefaultTask((Task)null);
//
//        System.out.println(tr.getStatusString("   ", 0));
//
//        long start = System.currentTimeMillis();
//        int clock = 0;
//
//        tr.start();
//
//        //while(!tr.isDone(true)){
//            tr.run();
//            clock++;
//        //}
//
//
//        int time = (int)(System.currentTimeMillis() - start);
//
//        System.out.println("Time - " + time);
//        System.out.println("Cycles - " + clock);
//        System.out.println("Time Per Cycle - " + (double)time/(double)clock);
    }
}