package us.nsakt.dynamicdatabase;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import java.util.List;

/**
 * A utility class to make debugging cleaner.
 *
 * @author Nick
 */
public class Debug {

    private static Class<?>[] classList;
    private static FilterMode filterMode;
    private static LogLevel lowLevel;

    public static void filter(LogLevel level, FilterMode mode, Class<?>... classes) {
        lowLevel = level;
        filterMode = mode;
        classList = classes;
    }

    public static void filter(String... classNames) {
        List<Class<?>> classes = Lists.newArrayList();
        for (String clazz : classNames) {
            try {
                classes.add(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                Debug.log(e);
            }
        }
    }

    public static void filter(FilterMode mode, Class<?>... classes) {
        filterMode = mode;
        classList = classes;
    }

    public static void filter(LogLevel level) {
        lowLevel = level;
    }

    public static void log(Exception e) {
        Validate.notNull(e, "e cannot be null");
        log(LogLevel.WARNING, ExceptionUtils.getFullStackTrace(e));
    }

    public static void log(LogLevel level, String string) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String className = elements[elements.length - 1].getClassName();
        if (classList != null && filterMode != null) {
            boolean foundMatch = (filterMode == FilterMode.BLACKLIST);
            for (Class<?> clazz : classList)
                if (clazz.getName().equals(className)) {
                    foundMatch = !foundMatch;
                    break;
                }
            if (!foundMatch) return;
        }
        if (lowLevel != null) {
            if (lowLevel.getLevel() > level.getLevel())
                return;
        }
        DateTime dTime = new DateTime();
        System.out.println("[" + level.name().toUpperCase() + " " + dTime.getHourOfDay() + ":" + dTime.getMinuteOfHour() + ":" + dTime.getSecondOfMinute() + "] " + string);
    }

    public enum FilterMode {
        WHITELIST, BLACKLIST;
    }

    /**
     * Enum to represent a log level.
     */
    public enum LogLevel {
        INFO(0), WARNING(1), SEVERE(2);

        int level;

        LogLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return this.level;
        }
    }
}