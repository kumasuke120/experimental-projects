package app.kumasuke.royce.linker;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * An helper that provides methods to add and execute sql statements with named parameters for updating
 */
public interface NamedBatchUpdateHelper extends BatchUpdateHelper {
    /**
     * Adds an execution for the given sql statement with all named parameters set to <code>null</code>.
     *
     * @return this <code>NamedBatchUpdateHelper</code> instance
     */
    default NamedBatchUpdateHelper addParameters() {
        return addParameters(null);
    }

    /**
     * Adds an execution for the given sql statement with given parameters.
     *
     * @param parameters <code>Map</code> that contains names and the corresponding parameters
     * @return this <code>NamedBatchUpdateHelper</code> instance
     */
    NamedBatchUpdateHelper addParameters(@Nullable Map<String, ?> parameters);

    /**
     * Adds an execution for the given sql statement with given bean. The properties of the given bean will be
     * treated as required names and the corresponding parameters
     *
     * @param bean bean that contains required names and the corresponding parameters
     * @return this <code>NamedBatchUpdateHelper</code> instance
     */
    NamedBatchUpdateHelper addParameters(@Nullable Object bean);
}
