package task;

import java.util.Hashtable;

import task.TaskRunner.Action;

public class Main{
    static Hashtable<Long, Long> cache = new Hashtable<>();

    public static long cacheFactorial(long n){
        if(n == 1) return 1;
        Long v = cache.get(n);
        if(v != null) return v; 
        v = n+cacheFactorial(n-1);
        cache.put(n,v);
        return v;
    }

    public static long factorial(long n){
        if(n == 1) return n;
        return n+factorial(n-1);
    }

    public static void main(String[] args){

        TaskRunner tr = new TaskRunner("runner 1");
        int tasks = 300;
        int maxTime = 300;
        int maxCycles = 300;


        for(int i = 0; i < tasks; i++){
            TimedTask timer = new TimedTask("Timer " + i);
            timer.addTimedStep(() -> {
                //System.out.println(timer.getName() + " with runtime: " + timer.getCurrentRunTime());
            }, (int)(Math.random() * maxTime));
        
            if(Math.random() <= 0.5)
                tr.addTask(timer, Action.ADD_TO_QUE);
            else
                tr.addTask(timer, Action.START);
        }

        for(int i = 0; i < tasks; i++){
            IncrimentedTask it = new IncrimentedTask("Inctirment " + i);
            it.addIncrimentedStep(() -> {
                //System.out.println(it.getName() + " with i: " + it.getI());
            }, (int)(Math.random() * maxCycles));
        
            if(Math.random() <= 0.5)
                tr.addTask(it, Action.ADD_TO_QUE);
            else
                tr.addTask(it, Action.START);
        }

        tr.setDefaultTask((Task)null);

        //System.out.println(tr.getStatusString("   ", 0));

        long start = System.currentTimeMillis();
        int clock = 0;

        //while(!tr.isDone(true)){
        //    tr.runRaw();
       //     clock++;
        //}

        long l;

        for(int i = 1; i < 3000; i++){
            l = cacheFactorial(i);
            clock++;
        }
        

        int time = (int)(System.currentTimeMillis() - start);

        System.out.println("Time - " + time);
        System.out.println("Cycles - " + clock);
        System.out.println("Time Per Cycle - " + (double)time/(double)clock);
        
    }
}