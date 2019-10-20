package app.kumasuke.royce.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * A singleton helper that manages singleton instances based on their only generic types
 *
 * @param <T> the class that has one generic type
 */
public class GenericSingletonContext<T> {
    private final ConcurrentMap<Class<?>, T> instances;
    private final Function<Class<?>, T> constructor;

    public GenericSingletonContext(Function<Class<?>, T> constructor) {
        this.instances = new ConcurrentHashMap<>();
        this.constructor = constructor;
    }

    public T getInstance(Class<?> genericType) {
        T instance = instances.get(genericType);

        if (instance == null) {
            instance = constructor.apply(genericType);
            final T oldInstance = instances.putIfAbsent(genericType, instance);
            instance = oldInstance == null ? instance : oldInstance;
        }

        return instance;
    }
}
