package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class ReadWritableLinkerImpl implements ReadWritableLinker {
    private final ReadableLinker rLinkerImpl;
    private final WritableLinker wLinkerImpl;

    ReadWritableLinkerImpl(Connection conn) {
        rLinkerImpl = new ReadableLinkerImpl(conn);
        wLinkerImpl = new WritableLinkerImpl(conn);
    }

    @Override
    public void close() {
        rLinkerImpl.close();
        wLinkerImpl.close();
    }

    @Override
    public <T> Optional<T> selectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql, @Nullable Object... parameters)
            throws SQLException {
        return rLinkerImpl.selectOne(mapper, sql, parameters);
    }

    @Override
    public <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                          @Nullable Map<String, ?> namedParameters) throws SQLException {
        return rLinkerImpl.namedSelectOne(mapper, sqlWithNames, namedParameters);
    }

    @Override
    public <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                          @Nullable Object bean) throws SQLException {
        return rLinkerImpl.namedSelectOne(mapper, sqlWithNames, bean);
    }

    @Override
    public <T> Stream<T> selectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                    @Nullable Object... parameters) throws SQLException {
        return rLinkerImpl.selectMany(mapper, sql, parameters);
    }

    @Override
    public <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                         @Nullable Map<String, ?> namedParameters) throws SQLException {
        return rLinkerImpl.namedSelectMany(mapper, sqlWithNames, namedParameters);
    }

    @Override
    public <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                         @Nullable Object bean) throws SQLException {
        return rLinkerImpl.namedSelectMany(mapper, sqlWithNames, bean);
    }

    @Override
    public int update(@Nonnull String sql, @Nullable Object... parameters) throws SQLException {
        return wLinkerImpl.update(sql, parameters);
    }

    @Override
    public int namedUpdate(@Nonnull String sqlWithNames, @Nullable Map<String, ?> namedParameters)
            throws SQLException {
        return wLinkerImpl.namedUpdate(sqlWithNames, namedParameters);
    }

    @Override
    public int namedUpdate(@Nonnull String sqlWithNames, @Nullable Object bean) throws SQLException {
        return wLinkerImpl.namedUpdate(sqlWithNames, bean);
    }

    @Override
    public <K> Optional<K> updateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                              @Nonnull String sql, Object... parameters) throws SQLException {
        return wLinkerImpl.updateAndReturnKey(keyMapper, sql, parameters);
    }

    @Override
    public <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                   @Nonnull String sqlWithNames,
                                                   @Nullable Map<String, ?> namedParameters) throws SQLException {
        return wLinkerImpl.namedUpdateAndReturnKey(keyMapper, sqlWithNames, namedParameters);
    }

    @Override
    public <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                   @Nonnull String sqlWithNames,
                                                   @Nullable Object bean) throws SQLException {
        return wLinkerImpl.namedUpdateAndReturnKey(keyMapper, sqlWithNames, bean);
    }

    @Override
    public VarargsBatchUpdateHelper batchUpdate(@Nonnull String sql) {
        return wLinkerImpl.batchUpdate(sql);
    }

    @Override
    public NamedBatchUpdateHelper namedBatchUpdate(@Nonnull String sqlWithNames) {
        return wLinkerImpl.namedBatchUpdate(sqlWithNames);
    }

    @Override
    public BatchExecuteHelper batchExecute() {
        return wLinkerImpl.batchExecute();
    }
}
