package net.njay.dynamicdatabase;

import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class to make debugging cleaner.
 *
 * @author Nick (Original Class)
 * @author Austin Mayes (Revamp)
 */
public class DebuggingService {

    private LinkedHashMap<FilterMode, List<Class<?>>> filters = Maps.newLinkedHashMap();
    private Logger logger;


    public DebuggingService(Logger logger) {
        this.logger = logger;
    }

    public void addFilter(FilterMode mode, List<Class<?>> classes) {
        this.filters.put(mode, classes);
    }

    public void log(Exception e) {
        Validate.notNull(e, "e cannot be null");
        log(LogLevel.WARNING, ExceptionUtils.getFullStackTrace(e));
    }

    public void log(LogLevel level, String string) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String className = elements[elements.length - 1].getClassName();
        if (this.filters != null) {
            boolean foundMatch = (this.filters.containsKey(FilterMode.BLACKLIST));
            for (Class<?> clazz : this.filters.get(FilterMode.BLACKLIST))
                if (clazz.getName().equals(className)) {
                    foundMatch = !foundMatch;
                    break;
                }
            if (!foundMatch) return;
        }
        this.logger.log(Level.ALL, "[" + level.name() + "]" + string);
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