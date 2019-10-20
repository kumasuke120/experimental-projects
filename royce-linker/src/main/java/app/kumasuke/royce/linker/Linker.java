package app.kumasuke.royce.linker;

/**
 * The main interface of <code>Royce-Liner</code> that is the base interface of all <code>Linker</code>s,
 * supporting <code>AutoCloseable</code>
 */
public interface Linker extends AutoCloseable {
    /**
     * Closes the <code>Linker</code>, without throwing any checked <code>Exception</code>.
     */
    @Override
    void close();
}
