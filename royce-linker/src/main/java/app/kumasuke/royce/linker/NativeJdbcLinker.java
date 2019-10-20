package app.kumasuke.royce.linker;

import java.sql.*;
import java.util.concurrent.Executor;

/**
 * A {@link Linker} that could do a series of database manipulations with native JDBC
 */
public interface NativeJdbcLinker extends Linker {
    /**
     * Gets the underlying jdbc {@link Connection} of this <code>Linker</code>.<br>
     * The returned <code>Connection</code> cannot be closed, invoke the following methods will throw
     * {@link UnsupportedOperationException}:
     * <ul>
     * <li>{@link Connection#abort(Executor)}</li>
     * <li>{@link Connection#close()}</li>
     * </ul>
     *
     * @return the underlying {@link Connection} with some limitations
     */
    Connection jdbcConnection();

    /**
     * Creates a {@link Statement} by the underlying jdbc {@link Connection} of this <code>Linker</code>, same as
     * {@code jdbcConnection().createStatement()}.
     *
     * @return newly created <code>Statement</code>
     * @throws SQLException error when creating the <code>Statement</code>
     */
    Statement statement() throws SQLException;

    /**
     * Creates a {@link PreparedStatement} by the underlying jdbc {@link Connection} of this <code>Linker</code>, same as
     * {@code jdbcConnection().prepareStatement(sql)}.
     *
     * @param sql the sql statement used to create <code>PreparedStatement</code>
     * @return newly created <code>PreparedStatement</code>
     * @throws SQLException error when creating the <code>PreparedStatement</code>
     */
    PreparedStatement preparedStatement(String sql) throws SQLException;

    /**
     * Creates a {@link CallableStatement} by the underlying jdbc {@link Connection} of this <code>Linker</code>, same as
     * {@code jdbcConnection().prepareCall(sql)}.
     *
     * @param sql the sql statement used to create <code>CallableStatement</code>
     * @return newly created <code>CallableStatement</code>
     * @throws SQLException error when creating the <code>CallableStatement</code>
     */
    CallableStatement callableStatement(String sql) throws SQLException;

    /**
     * Commits the current transaction of the underlying jdbc {@link Connection} of this <code>Linker</code>, same as
     * {@code jdbcConnection().commit()}.
     *
     * @throws SQLException error when committing
     */
    void commit() throws SQLException;

    /**
     * Rollbacks the current transaction of the underlying jdbc {@link Connection} of this <code>Linker</code>, same as
     * {@code jdbcConnection().commit()}.
     *
     * @throws SQLException error when rollback-ing
     */
    void rollback() throws SQLException;

    /**
     * Gets the {@link DatabaseMetaData} of the underlying jdbc {@link Connection} of this <code>Linker</code>, same as
     * {@code jdbcConnection().getMetaData()}.
     *
     * @return <code>DatabaseMetaData</code> of underlying <code>Connection</code>
     * @throws SQLException error when getting <code>DatabaseMetaData</code>
     */
    DatabaseMetaData databaseMetaData() throws SQLException;
}
