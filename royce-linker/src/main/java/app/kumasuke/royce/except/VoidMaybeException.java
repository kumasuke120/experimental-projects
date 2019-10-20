package app.kumasuke.royce.except;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Represents a value whose value could either be an exception of type <code>E</code> or nothing
 *
 * @param <E> the type of <code>Exception</code>
 */
public interface VoidMaybeException<E extends Exception> {
    /**
     * Handles exception in this {@code MaybeException<E, R>} if exists.
     *
     * @param handler a handler to handle <code>Exception</code> of type <code>E</code>
     */
    void handle(@Nonnull Consumer<E> handler);
}
