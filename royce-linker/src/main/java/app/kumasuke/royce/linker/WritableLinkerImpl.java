package app.kumasuke.royce.linker;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.BeanParametersParser;
import app.kumasuke.royce.util.MapParametersParser;
import app.kumasuke.royce.util.ParametersParser;
import app.kumasuke.royce.util.Statements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;

class WritableLinkerImpl extends ConnectionHolder implements WritableLinker {
    private static final Logger logger = LoggerFactory.getLogger(WritableLinker.class);

    WritableLinkerImpl(Connection conn) {
        super(conn);
    }

    @Override
    public void close() {
        super.releaseConnection();
    }

    @Override
    public int update(@Nonnull String sql, @Nullable Object... parameters) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            Statements.setParameters(ps, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            return ps.executeUpdate();
        }
    }

    @Override
    public int namedUpdate(@Nonnull String sqlWithNames, @Nullable Map<String, ?> namedParameters)
            throws SQLException {
        final MapParametersParser parser = new MapParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return update(sql, parameters);
    }

    @Override
    public int namedUpdate(@Nonnull String sqlWithNames, @Nullable Object bean) throws SQLException {
        final BeanParametersParser parser = new BeanParametersParser(sqlWithNames, bean);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return update(sql, parameters);
    }

    @Override
    public <K> Optional<K> updateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper, @Nonnull String sql,
                                              Object... parameters) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            Statements.setParameters(ps, parameters);
            Statements.debugSqlExecuting(logger, sql, parameters);
            ps.executeUpdate();
            return Statements.getKeyOne(ps, keyMapper);
        }
    }

    @Override
    public <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                   @Nonnull String sqlWithNames,
                                                   @Nullable Map<String, ?> namedParameters)
            throws SQLException {
        final MapParametersParser parser = new MapParametersParser(sqlWithNames, namedParameters);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return updateAndReturnKey(keyMapper, sql, parameters);
    }

    @Override
    public <K> Optional<K> namedUpdateAndReturnKey(@Nonnull ResultSetMapper<K> keyMapper,
                                                   @Nonnull String sqlWithNames,
                                                   @Nullable Object bean) throws SQLException {
        final BeanParametersParser parser = new BeanParametersParser(sqlWithNames, bean);
        parser.parse();

        final String sql = parser.getRawSql();
        final Object[] parameters = parser.getParameters();
        return updateAndReturnKey(keyMapper, sql, parameters);
    }

    @Override
    public BatchExecuteHelper batchExecute() {
        return new BatchExecuteHelperImpl();
    }

    @Override
    public VarargsBatchUpdateHelper batchUpdate(@Nonnull String sql) {
        return new VarargsBatchUpdateHelperImpl(sql);
    }

    @Override
    public NamedBatchUpdateHelper namedBatchUpdate(@Nonnull String sqlWithNames) {
        return new NamedBatchUpdateHelperImpl(sqlWithNames);
    }

    private class VarargsBatchUpdateHelperImpl implements VarargsBatchUpdateHelper {
        private final String sql;
        private final List<Object[]> parametersList;
        private boolean updated;

        VarargsBatchUpdateHelperImpl(String sql) {
            this.sql = sql;
            this.parametersList = new LinkedList<>();
            this.updated = false;
        }

        @Override
        public VarargsBatchUpdateHelper addParameters(@Nullable Object... parameters) {
            Statements.debugSqlAdding(logger, sql, parameters);
            parametersList.add(parameters);
            return this;
        }

        @Override
        public int[] performUpdate() throws SQLException {
            if (updated) {
                throw new IllegalStateException("updated has been performed");
            }

            updated = true;
            try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                return addAllAndExecuteBatch(ps);
            }
        }

        @Override
        public <T> Stream<T> performUpdateAndReturnKeys(ResultSetMapper<T> keyMapper) throws SQLException {
            if (updated) {
                throw new IllegalStateException("updated has been performed");
            }

            updated = true;
            try (PreparedStatement ps =
                         getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                addAllAndExecuteBatch(ps);
                return Statements.getKeyMany(ps, keyMapper);
            }
        }

        private int[] addAllAndExecuteBatch(PreparedStatement ps) throws SQLException {
            for (Object[] params : parametersList) {
                Statements.setParameters(ps, params);
                ps.addBatch();
            }
            logger.debug("batch executing added sql statements...");
            return ps.executeBatch();
        }
    }

    private class NamedBatchUpdateHelperImpl implements NamedBatchUpdateHelper {
        private final String sqlWithNames;

        private VarargsBatchUpdateHelper helper;
        private MapParametersParser mapParser;
        private BeanParametersParser beanParser;

        NamedBatchUpdateHelperImpl(String sqlWithNames) {
            this.sqlWithNames = sqlWithNames;
        }

        @Override
        public NamedBatchUpdateHelper addParameters(@Nullable Map<String, ?> parameters) {
            if (mapParser == null) {
                mapParser = new MapParametersParser(sqlWithNames, parameters);
            } else {
                mapParser.setNamedParameters(parameters);
            }
            mapParser.parse();
            return addParameters(mapParser);
        }

        @Override
        public NamedBatchUpdateHelper addParameters(@Nullable Object bean) {
            if (beanParser == null) {
                beanParser = new BeanParametersParser(sqlWithNames, bean);
            } else {
                beanParser.setBean(bean);
            }
            beanParser.parse();
            return addParameters(beanParser);
        }

        private NamedBatchUpdateHelper addParameters(ParametersParser<Object> parser) {
            if (helper == null) {
                String sql = parser.getRawSql();
                helper = new VarargsBatchUpdateHelperImpl(sql);
            }
            Object[] parameters = parser.getParameters();
            helper.addParameters(parameters);
            return this;
        }

        @Override
        public int[] performUpdate() throws SQLException {
            if (helper == null) {
                addParameters((Map<String, ?>) null);
            }
            return helper.performUpdate();
        }

        @Override
        public <T> Stream<T> performUpdateAndReturnKeys(ResultSetMapper<T> keyMapper) throws SQLException {
            if (helper == null) {
                addParameters((Map<String, ?>) null);
            }
            return helper.performUpdateAndReturnKeys(keyMapper);
        }
    }

    private class BatchExecuteHelperImpl implements BatchExecuteHelper {
        private final List<String> sqlList;
        private boolean updated;

        BatchExecuteHelperImpl() {
            this.sqlList = new LinkedList<>();
            this.updated = false;
        }

        @Override
        public BatchExecuteHelper add(@Nonnull String sql) {
            Statements.debugSqlAdding(logger, sql);
            sqlList.add(sql);
            return this;
        }

        @Override
        public BatchExecuteHelper addAll(@Nonnull Collection<String> sql) {
            for (String s : sql) {
                add(s);
            }
            return this;
        }

        @Override
        public BatchExecuteHelper addAll(@Nonnull String[] sql) {
            return addAll(Arrays.asList(sql));
        }

        @Override
        public int[] performUpdate() throws SQLException {
            if (updated) {
                throw new IllegalStateException("updated has been performed,");
            }

            updated = true;
            if (sqlList.isEmpty()) {
                return new int[0];
            } else {
                try (Statement stmt = getConnection().createStatement()) {
                    return addAllAndExecuteBatch(stmt);
                }
            }
        }

        @Override
        public <K> Stream<K> performUpdateAndReturnKeys(ResultSetMapper<K> keyMapper) throws SQLException {
            if (updated) {
                throw new IllegalStateException("updated has been performed,");
            }

            updated = true;
            if (sqlList.isEmpty()) {
                final List<K> empty = Collections.unmodifiableList(new LinkedList<>());
                return empty.stream();
            } else {
                try (Statement stmt = getConnection().createStatement()) {
                    addAllAndExecuteBatch(stmt);
                    return Statements.getKeyMany(stmt, keyMapper);
                }
            }
        }

        private int[] addAllAndExecuteBatch(Statement stmt) throws SQLException {
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            logger.debug("batch executing added sql statements...");
            return stmt.executeBatch();
        }
    }
}
