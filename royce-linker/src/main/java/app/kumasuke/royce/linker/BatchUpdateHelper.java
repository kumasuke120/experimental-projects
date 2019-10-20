package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

interface BatchUpdateHelper {
    /**
     * Executes all added sql statements in a batch way, returning an array of update counts.
     *
     * @return an array of update counts, same as the return value of {@link Statement#executeBatch()}.
     * @throws SQLException error when executing sql statements
     */
    int[] performUpdate() throws SQLException;

    /**
     * Executes all added sql statements in a batch way, returning a <code>Stream</code> of generated keys
     * converted by provided {@code ResultSetMapper<K>}.
     *
     * @param keyMapper {@code ResultSetMapper<K>} to convert returned <code>ResultSet</code> consisting of
     *                  <code>GENERATED_KEYS</code>
     * @param <K>       the type of converted keys
     * @return {@code Stream<K>} of converted keys
     * @throws SQLException error when executing sql statements
     * @see Statement#RETURN_GENERATED_KEYS
     */
    <K> Stream<K> performUpdateAndReturnKeys(ResultSetMapper<K> keyMapper) throws SQLException;
}
