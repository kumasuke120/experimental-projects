package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.CallParameter;
import app.kumasuke.royce.util.MapCallParametersParser;
import app.kumasuke.royce.util.Statements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class CallableLinkerImpl extends ConnectionHolder implements CallableLinker {
    private static final Logger logger = LoggerFactory.getLogger(CallableLinker.class);

    CallableLinkerImpl(Connection conn) {
        super(conn);
    }

    @Override
    public void close() {
        super.releaseConnection();
    }

    @Override
    public int call(@Nonnull String sql, @Nullable CallParameter... parameters) throws SQLException {
        try (CallableStatement cs = getConnection().prepareCall(sql)) {
            Statements.setParameters(cs, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            int retVal = cs.executeUpdate();
            Statements.getParameters(cs, parameters);
            return retVal;
        }
    }

    @Override
    public int namedCall(@Nonnull String sqlWithNames, @Nullable Map<String, CallParameter> namedParameters)
            throws SQLException {
        final MapCallParametersParser parser = new MapCallParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final CallParameter[] parameters = parser.getParameters();
        return call(sql, parameters);
    }

    @Override
    public <T> Optional<T> callAndReturnOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                            @Nullable CallParameter... parameters) throws SQLException {
        try (CallableStatement cs = getConnection().prepareCall(sql)) {
            Statements.setParameters(cs, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            Optional<T> retVal = Statements.getResultOne(cs, mapper);
            Statements.getParameters(cs, parameters);
            return retVal;
        }
    }

    @Override
    public <T> Optional<T> namedCallAndReturnOne(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                                 @Nullable Map<String, CallParameter> namedParameters) throws SQLException {
        final MapCallParametersParser parser = new MapCallParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final CallParameter[] parameters = parser.getParameters();
        return callAndReturnOne(mapper, sql, parameters);
    }

    @Override
    public <T> Stream<T> callAndReturnMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sql,
                                           @Nullable CallParameter... parameters) throws SQLException {
        try (CallableStatement cs = getConnection().prepareCall(sql)) {
            Statements.setParameters(cs, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            Stream<T> retVal = Statements.getResultMany(cs, mapper);
            Statements.getParameters(cs, parameters);
            return retVal;
        }
    }

    @Override
    public <T> Stream<T> namedCallAndReturnMany(@Nonnull ResultSetMapper<T> mapper, @Nonnull String sqlWithNames,
                                                @Nullable Map<String, CallParameter> namedParameters)
            throws SQLException {
        final MapCallParametersParser parser = new MapCallParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final CallParameter[] parameters = parser.getParameters();
        return callAndReturnMany(mapper, sql, parameters);
    }
}
