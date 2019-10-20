package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link Linker} that performs a series of operation to read from database
 */
public interface ReadableLinker extends Linker {
    /**
     * Executes given sql statement, converts the first row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>} if exists, and returns the value.
     *
     * @param mapper     {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sql        statement that executes a query
     * @param parameters optional sql parameters
     * @param <T>        type of return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    <T> Optional<T> selectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                              @Nullable Object... parameters) throws SQLException;

    /**
     * Executes given sql statement, converts the first row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>} if exists, and returns the value.
     *
     * @param mapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames statement that executes a query, which may contain named parameters
     * @param <T>          type of return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    default <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames)
            throws SQLException {
        return namedSelectOne(mapper, sqlWithNames, null);
    }

    /**
     * Executes given sql statement, converts the first row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>} if exists, and returns the value.
     *
     * @param mapper          {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames    statement that executes a query, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding parameters
     * @param <T>             type of return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                   @Nullable Map<String, ?> namedParameters) throws SQLException;

    /**
     * Executes given sql statement, converts the first row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>} if exists, and returns the value.
     *
     * @param mapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames statement that executes a query, which may contain named parameters
     * @param bean         bean that contains required names and the corresponding parameters
     * @param <T>          type of return value
     * @return the converted value of type {@code Optional<T>}
     * @throws SQLException error when executing the sql statement
     */
    <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                   @Nullable Object bean) throws SQLException;

    /**
     * Executes given sql statement, converts each row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>}, and returns a {@code Stream<T>} of converted values.
     *
     * @param mapper     {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sql        statement that executes a query
     * @param parameters optional sql parameters
     * @param <T>        type of return value
     * @return a {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    <T> Stream<T> selectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                             @Nullable Object... parameters) throws SQLException;

    /**
     * Executes given sql statement, converts each row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>}, and returns a {@code Stream<T>} of converted values.
     *
     * @param mapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames statement that executes a query, which may contain named parameters
     * @param <T>          type of return value
     * @return a {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    default <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper,
                                          @Nonnull String sqlWithNames) throws SQLException {
        return namedSelectMany(mapper, sqlWithNames, null);
    }

    /**
     * Executes given sql statement, converts each row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>}, and returns a {@code Stream<T>} of converted values.
     *
     * @param mapper          {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames    statement that executes a query, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding parameters
     * @param <T>             type of return value
     * @return a {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper,
                                  @Nonnull String sqlWithNames,
                                  @Nullable Map<String, ?> namedParameters) throws SQLException;

    /**
     * Executes given sql statement, converts each row to a value of type {@code Optional<T>} by the given
     * {@code ResultSetMapper<T>}, and returns a {@code Stream<T>} of converted values.
     *
     * @param mapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code>
     * @param sqlWithNames statement that executes a query, which may contain named parameters
     * @param bean         bean that contains required names and the corresponding parameters
     * @param <T>          type of return value
     * @return a {@code Stream<T>} of converted values
     * @throws SQLException error when executing the sql statement
     */
    <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper,
                                  @Nonnull String sqlWithNames,
                                  @Nullable Object bean) throws SQLException;
}
