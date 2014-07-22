package us.nsakt.dynamicdatabase;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * A nice channel-style debugging system
 *
 * @author molenzwiebel
 */
public enum Debug {
    EXCEPTION("Exception"), GENERIC("Generic"), MORPHIA("Morphia"), UNSPECIFIED_OUT_DEBUGGING("System.out debug");
    public static boolean PRINT_STACKTRACE = true;
    private static List<String> allowed = Lists.newArrayList();
    public String channel = "none";

    Debug(String chn) {
        this.channel = chn;
    }

    public static void allow(Debug deb) {
        allow(deb.channel);
    }

    public static void allow(String str) {
        allowed.add(str);
    }

    public static void replaceMainOutChannel() {
        PrintStream origOut = System.out;
        PrintStream interceptor = new Interceptor(origOut);
        System.setOut(interceptor);
    }

    public void debug(Object obj) {
        if (obj instanceof Throwable) {
            Throwable thr = (Throwable) obj;
            System.out.println("[DB] [Debug] [" + channel + "] EXCEPTION: " + thr.getMessage());
            if (PRINT_STACKTRACE) {
                System.out.println("[DB] [Debug] [" + channel + "] STACK TRACE:");
                thr.printStackTrace();
            }
        } else if (allowed.contains(this.channel))
            System.out.println("[DB] [Debug] [" + channel + "] " + obj.toString());
    }

    public void debugMembers(Object obj) {
        try {
            this.debug("Now printing debug information for object of class " + obj.getClass().getName());
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                this.debug(f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1) + ": " + f.get(obj).toString());
            }
        } catch (Exception e) {
            this.debug(e);
        }
    }

    private static class Interceptor extends PrintStream {
        public Interceptor(OutputStream out) {
            super(out, true);
        }

        @Override
        public void print(String s) {
            if (s.startsWith("[DB] [Debug]")) super.print(s);
            else {
                Bukkit.getLogger().severe("The following debug does not have a DebugType, and is therefore UNSPECIFIED. Please investigate.");
                Debug.UNSPECIFIED_OUT_DEBUGGING.debug(s);
            }
        }
    }
}
