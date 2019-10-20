package app.kumasuke.royce.linker;

import java.sql.Connection;

/**
 * A utility class that provides methods to create all kinds of <code>Linker</code>s
 */
public class Linkers {
    private Linkers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a instance of <code>ReadableLinker</code> with given <code>Connection</code>.
     *
     * @param conn <code>Connection</code> used within the <code>Linker</code>
     * @return a instance of <code>ReadableLinker</code>
     */
    public static ReadableLinker readable(Connection conn) {
        return new ReadableLinkerImpl(conn);
    }

    /**
     * Creates a instance of <code>WritableLinker</code> with given <code>Connection</code>.
     *
     * @param conn <code>Connection</code> used within the <code>Linker</code>
     * @return a instance of <code>WritableLinker</code>
     */
    public static WritableLinker writable(Connection conn) {
        return new WritableLinkerImpl(conn);
    }

    /**
     * Creates a instance of <code>ReadWritableLinker</code> with given <code>Connection</code>.
     *
     * @param conn <code>Connection</code> used within the <code>Linker</code>
     * @return a instance of <code>ReadWritableLinker</code>
     */
    public static ReadWritableLinker readWritable(Connection conn) {
        return new ReadWritableLinkerImpl(conn);
    }

    /**
     * Creates a instance of <code>CallableLinker</code> with given <code>Connection</code>.
     *
     * @param conn <code>Connection</code> used within the <code>Linker</code>
     * @return a instance of <code>CallableLinker</code>
     */
    public static CallableLinker callable(Connection conn) {
        return new CallableLinkerImpl(conn);
    }

    /**
     * Creates a instance of <code>UniversalLinker</code> with given <code>Connection</code>.
     *
     * @param conn <code>Connection</code> used within the <code>Linker</code>
     * @return a instance of <code>UniversalLinker</code>
     */
    public static UniversalLinker universal(Connection conn) {
        return new UniversalLinkerImpl(conn);
    }

    /**
     * Creates a instance of <code>NativeJdbcLinker</code> with given <code>Connection</code>.
     *
     * @param conn <code>Connection</code> used within the <code>Linker</code>
     * @return a instance of <code>NativeJdbcLinker</code>
     */
    public static NativeJdbcLinker nativeJdbc(Connection conn) {
        return new NativeJdbcLinkerImpl(conn);
    }
}
