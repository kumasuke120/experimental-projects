package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * An helper that provides methods to add and execute sql statements for updating
 */
public interface BatchExecuteHelper extends BatchUpdateHelper {
    /**
     * Add a sql statement which will be executed when {@link #performUpdate()} or
     * {@link #performUpdateAndReturnKeys(ResultSetMapper)} is called, along with other
     * added sql statements.
     *
     * @param sql sql statement to be executed
     * @return this <code>BatchExecuteHelper</code> instance
     */
    BatchExecuteHelper add(@Nonnull String sql);

    /**
     * Add one or more sql statement which will be executed when {@link #performUpdate()} or
     * {@link #performUpdateAndReturnKeys(ResultSetMapper)} is called, along with other
     * added sql statements.
     *
     * @param sql sql statements to be executed
     * @return this <code>BatchExecuteHelper</code> instance
     */
    BatchExecuteHelper addAll(@Nonnull Collection<String> sql);

    /**
     * Add one or more sql statement which will be executed when {@link #performUpdate()} or
     * {@link #performUpdateAndReturnKeys(ResultSetMapper)} is called, along with other
     * added sql statements.
     *
     * @param sql sql statements to be executed
     * @return this <code>BatchExecuteHelper</code> instance
     */
    BatchExecuteHelper addAll(@Nonnull String[] sql);
}
