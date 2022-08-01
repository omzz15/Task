package om.self.task.command;

public interface Commandable<COMMAND extends Enum, ARGS>{
    boolean runCommand(COMMAND command, ARGS... args);
}
