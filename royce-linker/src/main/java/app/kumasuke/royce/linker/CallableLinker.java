package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.CallParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link Linker} that performs a series of operation to cope with stored procedures
 */
public interface CallableLinker extends Linker {
    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * update count.
     *
     * @param sql        statement that calls a stored procedure
     * @param parameters <code>CallParameter</code>s
     * @return update count, same as {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    int call(@Nonnull final String sql, @Nullable CallParameter... parameters) throws SQLException;

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * update count.
     *
     * @param sqlWithNames statement that calls a stored procedure, which may contain named parameters
     * @return update count, same as {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    default int namedCall(@Nonnull final String sqlWithNames) throws SQLException {
        return namedCall(sqlWithNames, null);
    }

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * update count.
     *
     * @param sqlWithNames    statement that calls a stored procedure, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding <code>CallParameter</code>s
     * @return update count, same as {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    int namedCall(@Nonnull final String sqlWithNames,
                  @Nullable Map<String, CallParameter> namedParameters) throws SQLException;

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * a value of type {@code Optional<T>} that is converted by given {@code ResultSetMapper<T>}.
     *
     * @param mapper     {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sql        statement that calls a stored procedure
     * @param parameters <code>CallParameter</code>s
     * @param <T>        type of the return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    <T> Optional<T> callAndReturnOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull final String sql,
                                     @Nullable CallParameter... parameters) throws SQLException;

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * a value of type {@code Optional<T>} that is converted by given {@code ResultSetMapper<T>}.
     *
     * @param mapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames statement that calls a stored procedure, which may contain named parameters
     * @param <T>          type of the return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    default <T> Optional<T> namedCallAndReturnOne(@Nonnull ResultSetMapper<T> mapper,
                                                  @Nonnull final String sqlWithNames) throws SQLException {
        return namedCallAndReturnOne(mapper, sqlWithNames, null);
    }

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * a value of type {@code Optional<T>} that is converted by given {@code ResultSetMapper<T>}.
     *
     * @param mapper          {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames    statement that calls a stored procedure, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding <code>CallParameter</code>s
     * @param <T>             type of the return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    <T> Optional<T> namedCallAndReturnOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull final String sqlWithNames,
                                          @Nullable Map<String, CallParameter> namedParameters) throws SQLException;

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * a {@code Stream<T>} of values that are converted by given {@code ResultSetMapper<T>}.
     *
     * @param mapper     {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sql        statement that calls a stored procedure
     * @param parameters <code>CallParameter</code>s
     * @param <T>        type of return values
     * @return {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    <T> Stream<T> callAndReturnMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull final String sql,
                                    @Nullable CallParameter... parameters) throws SQLException;

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * a {@code Stream<T>} of values that are converted by given {@code ResultSetMapper<T>}.
     *
     * @param mapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames statement that calls a stored procedure, which may contain named parameters
     * @param <T>          type of return values
     * @return {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    default <T> Stream<T> namedCallAndReturnMany(@Nonnull ResultSetMapper<T> mapper,
                                                 @Nonnull final String sqlWithNames)
            throws SQLException {
        return namedCallAndReturnMany(mapper, sqlWithNames, null);
    }

    /**
     * Calls a stored procedure with the given sql statement and <code>CallParameter</code>, returning the
     * a {@code Stream<T>} of values that are converted by given {@code ResultSetMapper<T>}.
     *
     * @param mapper          {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames    statement that calls a stored procedure, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding <code>CallParameter</code>s
     * @param <T>             type of return values
     * @return {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    <T> Stream<T> namedCallAndReturnMany(@Nonnull ResultSetMapper<T> mapper,
                                         @Nonnull final String sqlWithNames,
                                         @Nullable Map<String, CallParameter> namedParameters) throws SQLException;
}
