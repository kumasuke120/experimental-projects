package app.kumasuke.royce.linker;

/**
 * A {@link Linker} that performs a series of operation to read from or write to database, and even to
 * cope with stored procedures
 */
public interface UniversalLinker extends ReadWritableLinker, CallableLinker {
}
