package app.kumasuke.royce;

import app.kumasuke.royce.linker.Linker;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * A helper interface to access <code>Linker</code> created by <code>Royce</code> without returning any value when
 * accessing
 *
 * @param <L> the type of {@link Linker Linker}
 * @see Royce
 */
@FunctionalInterface
public interface VoidAccessor<L extends Linker> {
    /**
     * Converts a {@code VoidAccessor<L>} to {@code Accessor<L, Void>} whose {@link Accessor#access(Linker) access}
     * method returns <code>null</code>.
     *
     * @param accessor the {@code VoidAccessor<L>} instance to be converted
     * @param <L>      the type of {@link Linker Linker}
     * @return {@code Accessor<L, Void>} version of <code>accessor</code>
     */
    @Nonnull
    static <L extends Linker> Accessor<L, Void> asAccessor(@Nonnull VoidAccessor<L> accessor) {
        return linker -> {
            accessor.access(linker);
            return null;
        };
    }

    /**
     * Accesses the <code>Linker</code> created by <code>Royce</code>, operates with the <code>Linker</code>.
     *
     * @param linker the <code>Linker</code> created by <code>Royce</code>
     * @throws SQLException any <code>SQLException</code> that may be thrown by calling methods of linker
     */
    void access(L linker) throws SQLException;
}
