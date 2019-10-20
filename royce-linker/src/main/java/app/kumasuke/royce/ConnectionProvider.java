package app.kumasuke.royce;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a provider to provide <code>Connection</code>
 */
@FunctionalInterface
public interface ConnectionProvider {
    /**
     * Returns a {@link Connection}.
     *
     * @return <code>Connection</code> instance
     * @throws SQLException exception that may be thrown when create or get the <code>Connection</code>
     */
    Connection getConnection() throws SQLException;
}
