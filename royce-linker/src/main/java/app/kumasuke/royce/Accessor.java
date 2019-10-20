package app.kumasuke.royce;

import app.kumasuke.royce.linker.Linker;

import java.sql.SQLException;

/**
 * A helper interface to access <code>Linker</code> created by <code>Royce</code>, which could return a value of type
 * {@code <R>} when accessing <code>Linker</code>
 *
 * @param <L> the type of {@link Linker Linker}
 * @param <R> the type of return value
 * @see Royce
 */
@FunctionalInterface
public interface Accessor<L extends Linker, R> {
    /**
     * Accesses the <code>Linker</code> created by <code>Royce</code>, operates with the <code>Linker</code> and return
     * any value of type {@code <R>}.
     *
     * @param linker the <code>Linker</code> created by <code>Royce</code>
     * @return any value of type {@code <R>}
     * @throws SQLException any <code>SQLException</code> that may be thrown by calling methods of linker
     */
    R access(L linker) throws SQLException;
}
