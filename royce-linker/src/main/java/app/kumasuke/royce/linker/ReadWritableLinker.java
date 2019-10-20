package app.kumasuke.royce.linker;

/**
 * A {@link Linker} that performs a series of operation to read from or write to database
 */
public interface ReadWritableLinker extends ReadableLinker, WritableLinker {
}
