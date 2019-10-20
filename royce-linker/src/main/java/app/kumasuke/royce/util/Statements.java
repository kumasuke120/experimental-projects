package app.kumasuke.royce.util;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.JdbcObjectValueGetHelper.CallableStatementValueGetSupport;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Statements {
    private Statements() {
        throw new UnsupportedOperationException();
    }

    public static void setParameters(PreparedStatement ps, Object... parameters) throws SQLException {
        if (parameters == null || parameters.length == 0) return;
        for (int i = 0; i < parameters.length; i++) {
            int index = i + 1;
            ps.setObject(index, parameters[i]);
        }
    }

    public static void setParameters(CallableStatement cs, CallParameter... parameters) throws SQLException {
        if (parameters == null || parameters.length == 0) return;
        for (int i = 0; i < parameters.length; i++) {
            CallParameter p = parameters[i];
            CallParameter.ParameterType pType = p.getParameterType();

            int index = i + 1;
            if (pType == CallParameter.ParameterType.OUT || pType == CallParameter.ParameterType.INOUT) {
                cs.registerOutParameter(index, p.getJdbcType());
            }
            if (pType == CallParameter.ParameterType.IN || pType == CallParameter.ParameterType.INOUT) {
                cs.setObject(index, p.getValue(), p.getJdbcType());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void getParameters(CallableStatement cs, CallParameter... parameters) throws SQLException {
        if (parameters == null || parameters.length == 0) return;
        for (int i = 0; i < parameters.length; i++) {
            CallParameter p = parameters[i];
            CallParameter.ParameterType pType = p.getParameterType();

            if (pType == CallParameter.ParameterType.OUT || pType == CallParameter.ParameterType.INOUT) {
                Class<?> vType = p.getValueType();
                Object value = getValue(cs, i + 1, vType);
                p.setValue(value);
            }
        }
    }

    private static Object getValue(CallableStatement callableStatement, int index, Class<?> javaType)
            throws SQLException {
        CallableStatementValueGetSupport valueGettable = new CallableStatementValueGetSupport(callableStatement);
        return JdbcObjectValueGetHelper.getValue(valueGettable, index, javaType);
    }

    public static <T> Optional<T> getResultOne(PreparedStatement ps, ResultSetMapper<T> mapper) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return ResultSets.mapResultOne(rs, mapper);
        }
    }

    public static <K> Optional<K> getKeyOne(Statement stmt, ResultSetMapper<K> mapper) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            return ResultSets.mapResultOne(rs, mapper);
        }
    }

    public static <T> Stream<T> getResultMany(PreparedStatement ps, ResultSetMapper<T> mapper) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return ResultSets.mapResultMany(rs, mapper);
        }
    }

    public static <K> Stream<K> getKeyMany(Statement stmt, ResultSetMapper<K> mapper) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            return ResultSets.mapResultMany(rs, mapper);
        }
    }

    public static void debugSqlAdding(Logger logger, String sql, Object... parameters) {
        if (logger.isDebugEnabled()) {
            if (parameters == null || parameters.length == 0) {
                logger.debug("adding sql: '" + sql + "'");
            } else {
                int[] indices = indexParameters(parameters);
                String numberedSql = numberBlankInSql(indices, sql);
                String parametersStr = parametersToString(indices, parameters);
                logger.debug("adding sql: '" + numberedSql + "' with parameter(s): " + parametersStr);
            }
        }
    }

    public static void debugSqlExecuting(Logger logger, String sql, Object... parameters) {
        if (logger.isDebugEnabled()) {
            if (parameters == null || parameters.length == 0) {
                logger.debug("executing sql: '" + sql + "'");
            } else {
                int[] indices = indexParameters(parameters);
                String numberedSql = numberBlankInSql(indices, sql);
                String parametersStr = parametersToString(indices, parameters);
                logger.debug("executing sql: '" + numberedSql + "' with parameter(s): " + parametersStr);
            }
        }
    }

    public static void debugSqlExecuting(Logger logger, String sql, CallParameter... parameters) {
        if (logger.isDebugEnabled()) {
            if (parameters == null || parameters.length == 0) {
                logger.debug("executing sql: '" + sql + "'");
            } else {
                int[] indices = indexParameters((Object[]) parameters);
                String numberedSql = numberBlankInSql(indices, sql);
                String parametersStr = parametersToString(indices, (Object[]) parameters);
                logger.debug("executing sql: '" + numberedSql + "' with call-parameter(s): " + parametersStr);
            }
        }
    }

    private static int[] indexParameters(Object... parameters) {
        Map<Integer, Integer> indexMap = new LinkedHashMap<>(parameters.length);

        int index = 0;
        for (int i = 0; i < parameters.length; i++) {
            if (!indexMap.containsKey(i)) {
                Object pi = parameters[i];
                for (int j = i + 1; j < parameters.length; j++) {
                    Object pj = parameters[j];
                    if (pi == pj) { // uses identity check
                        indexMap.put(j, index);
                    }
                }
                indexMap.put(i, index++);
            }
        }

        return indexMap.values()
                .stream()
                .mapToInt(i -> i)
                .toArray();
    }

    private static String numberBlankInSql(int[] indices, String sql) {
        StringBuilder result = new StringBuilder();

        int idx = 0;
        NumberState state = NumberState.READY;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            boolean doAppend = true;

            switch (state) {
                case READY: {
                    if (isStrDelimiter(c)) {
                        state = NumberState.IN_STR;
                    } else if (c == '?') {
                        if (idx < indices.length) {
                            doAppend = false;
                            result.append("{").append(indices[idx++]).append("}");
                        }
                    }
                    break;
                }
                case IN_STR: {
                    if (c == '\\') {
                        state = NumberState.IN_STR_ESCAPE;
                    } else if (isStrDelimiter(c)) {
                        state = NumberState.READY;
                    }
                    break;
                }
                case IN_STR_ESCAPE: {
                    state = NumberState.IN_STR;
                }
            }

            if (doAppend) {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static Object[] shrinkParameters(int[] indices, Object... parameters) {
        assert indices.length == parameters.length;

        int length = Arrays.stream(indices)
                .max().orElseGet(() -> indices.length - 1) + 1;
        Object[] shrunk = new Object[length];
        int nextIndex = 0;
        for (int i = 0; i < indices.length; i++) {
            int index = indices[i];
            if (index == nextIndex) {
                shrunk[nextIndex++] = parameters[i];
            }
        }
        return shrunk;
    }

    private static String parametersToString(int[] indices, Object... parameters) {
        StringBuilder result = new StringBuilder();
        result.append("{");

        Object[] shrunkParameters = shrinkParameters(indices, parameters);
        for (int i = 0; i < shrunkParameters.length; i++) {
            Object p = shrunkParameters[i];
            result.append(i).append(" = ").append(p);
            if (i != shrunkParameters.length - 1) {
                result.append(", ");
            }
        }

        result.append("}");
        return result.toString();
    }

    private static boolean isStrDelimiter(char c) {
        return c == '\'' || c == '`';
    }

    private enum NumberState {
        READY,
        IN_STR,
        IN_STR_ESCAPE
    }
}
