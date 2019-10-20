package app.kumasuke.royce.util;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A utility class make singleton pattern easy to apply, which uses lazy initialization with double-checked
 * locking.
 *
 * <p><b>Sample usage</b>:</p>
 * <pre>{@code
 * class KeyResource {
 *    private static final SingletonContext<KeyResource> singleton =
 *        new SingletonContext<>(KeyResource::new);
 *
 *    // ... omit instance fields and whatever ...
 *
 *    private KeyResource() {
 *        // do some initialization ...
 *    }
 *
 *    public static KeyResource getInstance() {
 *        return singleton.getInstance();
 *    }
 * }}
 * </pre>
 *
 * @param <T> the type of singleton instance
 */
public final class SingletonContext<T> {
    private final Supplier<T> constructor;
    private volatile T instance;

    /**
     * Creates an instance of <code>SingletonContext</code> with given constructor.
     *
     * @param constructor the constructor of {@code <T>} instance, cannot return null
     */
    public SingletonContext(@Nonnull Supplier<T> constructor) {
        this.constructor = constructor;
    }

    /**
     * Gets the singleton instance with lazy initialization at first time.
     *
     * @return singleton instance
     * @throws NullPointerException thrown if constructor returns null when performing initialization
     */
    public T getInstance() {
        T t;
        if ((t = instance) == null) {
            synchronized (this) {
                if ((t = instance) == null) {
                    t = Objects.requireNonNull(constructor.get(),
                                               "cannot construct null instance");
                    instance = t;
                }
            }
        }

        return t;
    }
}
