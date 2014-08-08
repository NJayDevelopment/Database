package us.nsakt.dynamicdatabase.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionExecutor {
    private static String NMS_PATH = "net.minecraft.server." + (
                                                                Bukkit.getServer() != null ? Bukkit.getServer()
                                                                                                   .getClass()
                                                                                                   .getPackage()
                                                                                                   .getName()
                                                                                                   .replace(".", ",").split(",")[3] : "UNKNOWN"
    );

    public static Object execute(String command, Object toCallOn, Object... args) throws Exception {
        String[] parts = command.split("\\.");

        Object obj = toCallOn;

        for (String part : parts) {
            if (part.indexOf('(') != -1) {
                if (part.charAt(part.indexOf('(') + 1) == ')') {
                    obj = getObjByFunction(obj, part.substring(0, part.length() - 2));
                } else {
                    String[] arguments = part.substring(part.indexOf('(') + 1, part.indexOf(')')).split(", ");

                    Object[] params = new Object[arguments.length];

                    int i = 0;
                    for (String arg : arguments) {
                        params[i++] = args[Integer.parseInt(arg.replace("{", "").replace("}", "")) - 1];
                    }

                    obj = getObjByFunction(obj, part.substring(0, part.indexOf('(')), params);
                }
            } else {
                obj = getObj(obj, part);
            }
        }
        return obj;
    }

    protected static Object getObjByFunction(Object obj, String methodName) throws Exception {
        Method m = null;
        Class<?> c = obj.getClass();
        for (c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.getName().equals(methodName))
                    m = method;
            }
        }
        m.setAccessible(true);
        return m.invoke(obj);
    }

    protected static Object getObjByFunction(Object obj, String name, Object[] args) throws Exception {
        Method m = null;
        Class<?> c = obj.getClass();
        for (c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.getName().equals(name) && checkForMatch(method.getParameterTypes(), args))
                    m = method;
            }
        }
        if (m == null)
            throw new IllegalArgumentException("Could not find function " + name + " with arguments " + Arrays.toString(args) + " on object " + obj.getClass().getName());
        m.setAccessible(true);
        return m.invoke(obj, args);
    }

    protected static Object getObj(Object obj, String fieldName) throws Exception {
        Field f = null;
        Class<?> c = obj.getClass();
        for (c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().equals(fieldName))
                    f = field;
            }
        }
        f.setAccessible(true);
        return f.get(obj);
    }

    private static boolean checkForMatch(Class<?>[] classes, Object[] args) {
        if (classes.length != args.length) return false;
        int i = 0;
        for (Class<?> cls : classes) {
            Object obj = args[i++];
            if (!cls.isAssignableFrom(obj.getClass()) && !isPrimitiveWrapper(cls, obj.getClass())) return false;
        }
        return true;
    }

    private static boolean isPrimitiveWrapper(Class<?> class1, Class<?> class2) {
        if ((class1 == Integer.class && class2 == int.class) || (class1 == int.class && class2 == Integer.class))
            return true;
        if ((class1 == Boolean.class && class2 == boolean.class) || (class1 == boolean.class && class2 == Boolean.class))
            return true;
        if ((class1 == Character.class && class2 == char.class) || (class1 == char.class && class2 == Character.class))
            return true;
        if ((class1 == Float.class && class2 == float.class) || (class1 == float.class && class2 == Float.class))
            return true;
        if ((class1 == Double.class && class2 == double.class) || (class1 == double.class && class2 == Double.class))
            return true;
        if ((class1 == Long.class && class2 == long.class) || (class1 == long.class && class2 == Long.class))
            return true;
        if ((class1 == Short.class && class2 == short.class) || (class1 == short.class && class2 == Short.class))
            return true;
        if ((class1 == Byte.class && class2 == byte.class) || (class1 == byte.class && class2 == Byte.class))
            return true;
        return false;
    }

    private static Constructor<?> getMatchingConstructor(Class<?> cls, Object... args) {
        for (Constructor<?> constr : cls.getConstructors()) {
            if (checkForMatch(constr.getParameterTypes(), args)) return constr;
        }
        throw new IllegalArgumentException("Could not create a ReflectionObject for class " + cls.getName() + ": No " +
                                           "matching constructor found!");
    }

    public static String getNMS_PATH() {
        return NMS_PATH;
    }

    public static void setNMS_PATH(String NMS_PATH) {
        ReflectionExecutor.NMS_PATH = NMS_PATH;
    }

    public static class ReflectionObject {
        private Object object;

        public ReflectionObject(Object obj) {
            this.object = obj;
        }

        public static ReflectionObject fromNMS(String className, Object... args) {
            try {
                Class<?> clazz = Class.forName(getNMS_PATH() + "." + className);
                return new ReflectionObject(getMatchingConstructor(clazz, args).newInstance(args));
            } catch (Exception e) {
                try {
                    Class<?> clazz = Class.forName(className);
                    return new ReflectionObject(getMatchingConstructor(clazz, args).newInstance(args));
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Could not create a ReflectionObject for class " + className,
                                                       ex);
                }
            }
        }

        public Object fetch() {
            return object;
        }

        public ReflectionObject invoke(String methodName, Object... args) {
            try {
                Object obj = getObjByFunction(object, methodName, args);
                if (obj != null)
                    return new ReflectionObject(obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not invoke " + methodName + " on object of class " + this.object.getClass().getName(), e);
            }
            return null;
        }

        public ReflectionObject getAsRO(String field) {
            return new ReflectionObject(get(field));
        }

        public Object get(String field) {
            try {
                return getObj(object, field);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not get " + field + " on object of class " + this.object
                                                                                                        .getClass().getName(), e);
            }
        }

        public <T> T fetchAs(Class<T> as) {
            try {
                return castOrCreate(object, as);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @SuppressWarnings("unchecked")
        private <T> T castOrCreate(Object obj, Class<T> as) throws Exception {
            try {
                if (as.isAssignableFrom(obj.getClass()))
                    return (T) obj;

                Constructor<?> constr = getMatchingConstructor(as, obj);
                if (constr != null)
                    return (T) constr.newInstance(obj);

                throw new IllegalArgumentException("Could not convert object of class " + obj.getClass().getName() +
                                                   " to " + as.getName());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public <T> T get(String field, Class<T> as) {
            try {
                Object obj = get(field);
                return castOrCreate(obj, as);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public void set(String fieldName, Object arg) {
            try {
                Field f = null;
                Class<?> c = object.getClass();
                for (c = object.getClass(); c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        if (field.getName().equals(fieldName))
                            f = field;
                    }
                }
                f.setAccessible(true);
                f.set(object, arg);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not set " + fieldName + " on object of class " + this.object.getClass().getName(), e);
            }
        }
    }
}