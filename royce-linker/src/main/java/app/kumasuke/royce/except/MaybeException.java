package app.kumasuke.royce.except;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Represents a value whose value could either be an exception of type <code>E</code> or
 * an instance of type <code>R</code>
 *
 * @param <E> the type of <code>Exception</code>
 * @param <R> the type of the actual value
 */
public interface MaybeException<E extends Exception, R> {
    /**
     * Converts a {@code MaybeException<E, R>} to {@code VoidMaybeException<E>} whose value will be ignored when
     * <code>handle</code>d.
     *
     * @param maybeException the {@code MaybeException<E, R>} instance to be converted
     * @param <E>            the type of <code>Exception</code>
     * @param <R>            the type of the original value
     * @return {@code VoidMaybeException<E>} version of <code>maybeException</code>
     */
    @Nonnull
    static <E extends Exception, R> VoidMaybeException<E> asVoid(@Nonnull MaybeException<E, R> maybeException) {
        return handler -> maybeException.handle(e -> {
            handler.accept(e);
            return null;
        });
    }

    /**
     * Handles exception in this {@code MaybeException<E, R>} if exists.
     * Returns the actual value if there is no exception, or the return value of <code>handler</code> otherwise.
     *
     * @param handler a handler to handle <code>Exception</code> of type <code>E</code>, and return value of
     *                type <code>R</code> that will be final result of this whole method
     * @return the actual value or the return value of <code>handler</code>
     */
    R handle(@Nonnull Function<E, R> handler);
}
