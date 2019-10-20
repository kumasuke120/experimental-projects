package app.kumasuke.royce.except;

import java.sql.SQLException;

/**
 * Wraps a <code>SQLException</code> to make it a unchecked exception
 */
public class UncheckedSQLException extends RuntimeException {
    public UncheckedSQLException(SQLException e) {
        super(e);
    }

    /**
     * Returns the underlying <code>SQLException</code> that this instance wraps.
     *
     * @return cause of type <code>SQLException</code>
     */
    @Override
    public SQLException getCause() {
        return (SQLException) super.getCause();
    }
}
