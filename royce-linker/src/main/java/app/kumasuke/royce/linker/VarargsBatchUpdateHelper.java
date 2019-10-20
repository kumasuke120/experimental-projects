package app.kumasuke.royce.linker;

import javax.annotation.Nullable;

/**
 * An helper that provides methods to add and execute sql statements with parameters for updating
 */
public interface VarargsBatchUpdateHelper extends BatchUpdateHelper {
    /**
     * Adds an execution for the given sql statement with given parameters.
     *
     * @param parameters optional sql parameters
     * @return this <code>VarargsBatchUpdateHelper</code> instance
     */
    VarargsBatchUpdateHelper addParameters(@Nullable Object... parameters);
}
