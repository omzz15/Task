package om.self.task.command;

public interface KeyedCommandable <KEY, COMMAND extends Enum, ARGS>{
    boolean runKeyedCommand(KEY key, COMMAND command, ARGS... args);
}
