package app.kumasuke.royce.mapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper converting <code>ResultSet</code> to required type {@code <T>}
 *
 * @param <T> the required type
 */
@FunctionalInterface
public interface ResultSetMapper<T> {
    /**
     * Converts given <code>ResultSet</code> to required type {@code <T>}.
     *
     * @param rs given <code>ResultSet</code> on which conversion performs
     * @return converted value of type {@code <T>}
     * @throws SQLException errors happened when converting
     */
    T mapRow(@Nonnull ResultSet rs) throws SQLException;
}
