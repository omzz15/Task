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
        Group main = new Group("main");
        Command command = new Command("command", main){
            public void onRun() {
                System.out.println("command run");
            }

            public void start(){
                System.out.println("command start");
            }

            public void stop(boolean isInterrupted){
                System.out.println("command stop: " + isInterrupted);
            }

            public boolean isFinished(){
                return true;
            }
        };
        command.runCommand(Group.Command.START);

        IncrementedTask incrementedTask = new IncrementedTask("incrementedTask", main);
        incrementedTask.addIncrementedStep(()->{}, 100);
        incrementedTask.addStep(()->command.runCommand(Group.Command.PAUSE));

        while (!main.isDone()) {
            main.run();
        }
    }
}