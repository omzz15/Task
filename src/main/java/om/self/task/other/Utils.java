package om.self.task.other;

/**
 * A class with some useful methods
 */
public class Utils {
    /**
     * repeat a string a certain number of times
     * @param base the string to repeat
     * @param times the number of times to repeat it
     * @return the repeated string
     */
    public static String repeat(String base, int times){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(base);
        }
        return builder.toString();
    }
}
