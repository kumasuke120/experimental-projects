package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.CallParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class UniversalLinkerImpl implements UniversalLinker {
    private final ReadWritableLinker rwLinkerImpl;
    private final CallableLinker cLinkerImpl;

    UniversalLinkerImpl(Connection conn) {
        this.rwLinkerImpl = new ReadWritableLinkerImpl(conn);
        this.cLinkerImpl = new CallableLinkerImpl(conn);
    }

    @Override
    public void close() {
        rwLinkerImpl.close();
        cLinkerImpl.close();
    }

    @Override
    public <T> Optional<T> selectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql, @Nullable Object... parameters)
            throws SQLException {
        return rwLinkerImpl.selectOne(mapper, sql, parameters);
    }

    @Override
    public <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                          @Nullable Map<String, ?> namedParameters) throws SQLException {
        return rwLinkerImpl.namedSelectOne(mapper, sqlWithNames, namedParameters);
    }

    @Override
    public <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                          @Nullable Object bean) throws SQLException {
        return rwLinkerImpl.namedSelectOne(mapper, sqlWithNames, bean);
    }

    @Override
    public <T> Stream<T> selectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                    @Nullable Object... parameters) throws SQLException {
        return rwLinkerImpl.selectMany(mapper, sql, parameters);
    }

    @Override
    public <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                         @Nullable Map<String, ?> namedParameters) throws SQLException {
        return rwLinkerImpl.namedSelectMany(mapper, sqlWithNames, namedParameters);
    }

    @Override
    public <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                         @Nullable Object bean) throws SQLException {
        return rwLinkerImpl.namedSelectMany(mapper, sqlWithNames, bean);
    }

    @Override
    public int update(@Nonnull String sql, @Nullable Object... parameters) throws SQLException {
        return rwLinkerImpl.update(sql, parameters);
    }

    @Override
    public int namedUpdate(@Nonnull String sqlWithNames, @Nullable Map<String, ?> namedParameters)
            throws SQLException {
        return rwLinkerImpl.namedUpdate(sqlWithNames, namedParameters);
    }

    @Override
    public int namedUpdate(@Nonnull String sqlWithNames, @Nullable Object bean) throws SQLException {
        return rwLinkerImpl.namedUpdate(sqlWithNames, bean);
    }

    @Override
    public <K> Optional<K> updateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                              @Nonnull String sql, Object... parameters) throws SQLException {
        return rwLinkerImpl.updateAndReturnKey(keyMapper, sql, parameters);
    }

    @Override
    public <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                   @Nonnull String sqlWithNames,
                                                   @Nullable Map<String, ?> namedParameters) throws SQLException {
        return rwLinkerImpl.namedUpdateAndReturnKey(keyMapper, sqlWithNames, namedParameters);
    }

    @Override
    public <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                   @Nonnull String sqlWithNames,
                                                   @Nullable Object bean) throws SQLException {
        return rwLinkerImpl.namedUpdateAndReturnKey(keyMapper, sqlWithNames, bean);
    }

    @Override
    public VarargsBatchUpdateHelper batchUpdate(@Nonnull String sql) {
        return rwLinkerImpl.batchUpdate(sql);
    }

    @Override
    public NamedBatchUpdateHelper namedBatchUpdate(@Nonnull String sqlWithNames) {
        return rwLinkerImpl.namedBatchUpdate(sqlWithNames);
    }

    @Override
    public BatchExecuteHelper batchExecute() {
        return rwLinkerImpl.batchExecute();
    }

    @Override
    public int call(@Nonnull String sql, @Nullable CallParameter... parameters) throws SQLException {
        return cLinkerImpl.call(sql, parameters);
    }

    @Override
    public int namedCall(@Nonnull String sqlWithNames, @Nullable Map<String, CallParameter> namedParameters)
            throws SQLException {
        return cLinkerImpl.namedCall(sqlWithNames, namedParameters);
    }

    @Override
    public <T> Optional<T> callAndReturnOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                            @Nullable CallParameter... parameters) throws SQLException {
        return cLinkerImpl.callAndReturnOne(mapper, sql, parameters);
    }

    @Override
    public <T> Optional<T> namedCallAndReturnOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                                 @Nullable Map<String, CallParameter> namedParameters) throws SQLException {
        return cLinkerImpl.namedCallAndReturnOne(mapper, sqlWithNames, namedParameters);
    }

    @Override
    public <T> Stream<T> callAndReturnMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                           @Nullable CallParameter... parameters) throws SQLException {
        return cLinkerImpl.callAndReturnMany(mapper, sql, parameters);
    }

    @Override
    public <T> Stream<T> namedCallAndReturnMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                                @Nullable Map<String, CallParameter> namedParameters)
            throws SQLException {
        return cLinkerImpl.namedCallAndReturnMany(mapper, sqlWithNames, namedParameters);
    }
}
