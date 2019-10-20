package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.BeanParametersParser;
import app.kumasuke.royce.util.MapParametersParser;
import app.kumasuke.royce.util.Statements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class ReadableLinkerImpl extends ConnectionHolder implements ReadableLinker {
    private static final Logger logger = LoggerFactory.getLogger(ReadableLinker.class);

    ReadableLinkerImpl(Connection conn) {
        super(conn);
    }

    @Override
    public void close() {
        super.releaseConnection();
    }

    @Override
    public <T> Optional<T> selectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                     @Nullable Object... parameters)
            throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            Statements.setParameters(ps, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            return Statements.getResultOne(ps, mapper);
        }
    }

    @Override
    public <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                          @Nullable Map<String, ?> namedParameters) throws SQLException {
        final MapParametersParser parser = new MapParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return selectOne(mapper, sql, parameters);
    }

    @Override
    public <T> Optional<T> namedSelectOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                          @Nullable Object bean) throws SQLException {
        final BeanParametersParser parser = new BeanParametersParser(sqlWithNames, bean);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return selectOne(mapper, sql, parameters);
    }

    @Override
    public <T> Stream<T> selectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                    @Nullable Object... parameters) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            Statements.setParameters(ps, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            return Statements.getResultMany(ps, mapper);
        }
    }

    @Override
    public <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                         @Nullable Map<String, ?> namedParameters) throws SQLException {
        final MapParametersParser parser = new MapParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return selectMany(mapper, sql, parameters);
    }

    @Override
    public <T> Stream<T> namedSelectMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                         @Nullable Object bean) throws SQLException {
        final BeanParametersParser parser = new BeanParametersParser(sqlWithNames, bean);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return selectMany(mapper, sql, parameters);
    }
}
