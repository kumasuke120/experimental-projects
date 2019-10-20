package app.kumasuke.test.util;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class Reflects {
    private Reflects() {
        throw new UnsupportedOperationException();
    }

    public static <T> T newInstance(String className, Object... parameters) throws InvocationTargetException {
        try {
            // converts type for the caller, cast error should be taken care of on call site
            @SuppressWarnings("unchecked")
            Class<T> theClass = (Class<T>) Class.forName(className);
            return newInstance(theClass, parameters);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static <T> T newInstance(Class<T> theClass, Object... parameters) throws InvocationTargetException {
        // constructor must return the type of T
        @SuppressWarnings("unchecked")
        Constructor<T>[] constructors = (Constructor<T>[]) theClass.getDeclaredConstructors();
        for (Constructor<T> c : constructors) {
            Class[] types = c.getParameterTypes();
            if (areTypesMatched(types, parameters)) {
                try {
                    if (!c.isAccessible()) {
                        c.setAccessible(true);
                    }

                    return c.newInstance(parameters);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new AssertionError(e);
                }
            }
        }

        throw new IllegalArgumentException();
    }

    public static Object invokeStatic(String className, String methodName, Object... parameters)
            throws InvocationTargetException {
        try {
            Class<?> theClass = Class.forName(className);
            return invokeStatic(theClass, methodName, parameters);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private static Object invokeStatic(Class<?> theClass, String methodName, Object... parameters)
            throws InvocationTargetException {
        Method[] methods = theClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName) && Modifier.isStatic(m.getModifiers())) {
                Class<?>[] expectedTypes = m.getParameterTypes();
                if (areTypesMatched(expectedTypes, parameters)) {
                    if (!m.isAccessible()) {
                        m.setAccessible(true);
                    }

                    try {
                        return m.invoke(null, parameters);
                    } catch (IllegalAccessException e) {
                        throw new AssertionError(e);
                    }
                }
            }
        }

        throw new IllegalArgumentException();
    }

    public static Object invokeVirtual(Object object, String methodName, Object... parameters)
            throws InvocationTargetException {
        Class<?> theClass = object.getClass();

        Method[] methods = theClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName) && !Modifier.isStatic(m.getModifiers())) {
                Class<?>[] expectedTypes = m.getParameterTypes();
                if (areTypesMatched(expectedTypes, parameters)) {
                    if (!m.isAccessible()) {
                        m.setAccessible(true);
                    }

                    try {
                        return m.invoke(object, parameters);
                    } catch (IllegalAccessException e) {
                        throw new AssertionError(e);
                    }
                }
            }
        }

        throw new IllegalArgumentException();
    }

    private static boolean areTypesMatched(Class[] expectedTypes, Object[] parameters) {
        if (expectedTypes.length == parameters.length) {
            for (int i = 0; i < expectedTypes.length; i++) {
                Class<?> expectedType = expectedTypes[i];
                Object parameter = parameters[i];

                if (parameter != null && !expectedType.isAssignableFrom(parameter.getClass())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static Object invokeWithDefaultArguments(Method method, Object object)
            throws InvocationTargetException {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        Object[] parameters = Arrays.stream(method.getParameterTypes())
                .map(Reflects::getTypeDefaultValue)
                .toArray(Object[]::new);
        try {
            return method.invoke(object, parameters);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public static void setField(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();

        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(object, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public static Stream<Method> getPublicInstanceMethods(Object object) {
        Class<?> clazz = object.getClass();
        return getPublicInstanceMethods(clazz);
    }

    private static Stream<Method> getPublicInstanceMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        return Arrays.stream(methods)
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .filter(m -> !"equals".equals(m.getName()))
                .filter(m -> !"hashCode".equals(m.getName()))
                .filter(m -> !"toString".equals(m.getName()))
                .filter(m -> !"wait".equals(m.getName()))
                .filter(m -> !"notify".equals(m.getName()))
                .filter(m -> !"notifyAll".equals(m.getName()))
                .filter(m -> !"getClass".equals(m.getName()));
    }

    public static Object getTypeDefaultValue(Class<?> clazz) {
        if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return 0;
        } else if (byte.class.equals(clazz) || Byte.class.equals(clazz)) {
            return (byte) 0;
        } else if (short.class.equals(clazz) || Short.class.equals(clazz)) {
            return (short) 0;
        } else if (char.class.equals(clazz) || Character.class.equals(clazz)) {
            return (char) 0;
        } else if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return 0L;
        } else if (double.class.equals(clazz) || Double.class.equals(clazz)) {
            return 0D;
        } else if (float.class.equals(clazz) || Float.class.equals(clazz)) {
            return 0F;
        } else if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            return false;
        } else {
            return null;
        }
    }
}
