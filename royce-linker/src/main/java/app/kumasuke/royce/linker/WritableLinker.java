package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link Linker} that performs a series of operation to write to database
 */
public interface WritableLinker extends Linker {
    /**
     * Executes given statement with given parameters, returning the update count of execution.
     *
     * @param sql        statement that executes an update
     * @param parameters optional sql parameters
     * @return the update count of executing given sql statement, which is same as the return value of
     * {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    int update(@Nonnull String sql, @Nullable Object... parameters) throws SQLException;

    /**
     * Executes given statement with given named parameters, returning the update count of execution.
     *
     * @param sqlWithNames statement that executes an update, which may contain named parameters
     * @return the update count of executing given sql statement, which is same as the return value of
     * {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    default int namedUpdate(@Nonnull String sqlWithNames) throws SQLException {
        return namedUpdate(sqlWithNames, null);
    }

    /**
     * Executes given statement with given named parameters, returning the update count of execution.
     *
     * @param sqlWithNames    statement that executes an update, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding parameters
     * @return the update count of executing given sql statement, which is same as the return value of
     * {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    int namedUpdate(@Nonnull String sqlWithNames, @Nullable Map<String, ?> namedParameters)
            throws SQLException;

    /**
     * Executes given statement with given named parameters, returning the update count of execution.
     *
     * @param sqlWithNames statement that executes an update, which may contain named parameters
     * @param bean         bean that contains required names and the corresponding parameters
     * @return the update count of executing given sql statement, which is same as the return value of
     * {@link Statement#executeUpdate(String)}
     * @throws SQLException error when executing the sql statement
     */
    int namedUpdate(@Nonnull String sqlWithNames, @Nullable Object bean) throws SQLException;

    /**
     * Executes given statement with given parameters, returning a generated key converted by provided
     * {@code ResultSetMapper<K>}.
     *
     * @param keyMapper  {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code> consisting of
     *                   <code>GENERATED_KEYS</code>
     * @param sql        statement that executes an update
     * @param parameters optional sql parameters
     * @param <K>        the type of the converted key
     * @return {@code Optional<K>} of the converted key
     * @throws SQLException error when executing the sql statement
     */
    <K> Optional<K> updateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper, @Nonnull String sql,
                                       Object... parameters) throws SQLException;

    /**
     * Executes given statement with given named parameters, returning a generated key converted by provided
     * {@code ResultSetMapper<K>}.
     *
     * @param keyMapper    {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code> consisting of
     *                     <code>GENERATED_KEYS</code>
     * @param sqlWithNames statement that executes an update, which may contain named parameters
     * @param <K>          the type of the converted key
     * @return {@code Optional<K>} of the converted key
     * @throws SQLException error when executing the sql statement
     */
    default <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                    @Nonnull String sqlWithNames) throws SQLException {
        return namedUpdateAndReturnKey(keyMapper, sqlWithNames, null);
    }

    /**
     * Executes given statement with given named parameters, returning a generated key converted by provided
     * {@code ResultSetMapper<K>}.
     *
     * @param keyMapper       {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code> consisting of
     *                        <code>GENERATED_KEYS</code>
     * @param sqlWithNames    statement that executes an update, which may contain named parameters
     * @param namedParameters <code>Map</code> that contains names and the corresponding parameters
     * @param <K>             the type of the converted key
     * @return {@code Optional<K>} of the converted key
     * @throws SQLException error when executing the sql statement
     */
    <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper, @Nonnull String sqlWithNames,
                                            @Nullable Map<String, ?> namedParameters) throws SQLException;

    /**
     * Executes given statement with given named parameters, returning a generated key converted by provided
     * {@code ResultSetMapper<K>}.
     *
     * @param keyMapper    {@code ResultSetMapper<T>} to convert returned <code>ResultSet</code> consisting of
     *                     <code>GENERATED_KEYS</code>
     * @param sqlWithNames statement that executes an update, which may contain named parameters
     * @param bean         bean that contains required names and the corresponding parameters
     * @param <K>          the type of the converted key
     * @return {@code Optional<K>} of the converted key
     * @throws SQLException error when executing the sql statement
     */
    <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper, @Nonnull String sqlWithNames,
                                            @Nullable Object bean) throws SQLException;

    /**
     * Starts to batch execute given statement, returning a helper class to add extra parameters.
     *
     * @param sql statement that executes an update
     * @return a helper class to add extra parameters and to batch execute given sql statement
     */
    VarargsBatchUpdateHelper batchUpdate(@Nonnull String sql);

    /**
     * Starts to batch execute given statement, returning a helper class to add extra named parameters.
     *
     * @param sqlWithNames statement that executes an update, which may contain named parameters
     * @return a helper class to add extra parameters and to batch execute given sql statement
     */
    NamedBatchUpdateHelper namedBatchUpdate(@Nonnull String sqlWithNames);

    /**
     * Starts to batch execute sql statements, returning a helper class to add sql statements that need
     * executing.
     *
     * @return a helper class to add and to batch execute given sql statements
     */
    BatchExecuteHelper batchExecute();
}
