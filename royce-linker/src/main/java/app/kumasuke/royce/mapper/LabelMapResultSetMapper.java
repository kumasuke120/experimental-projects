package app.kumasuke.royce.mapper;

import app.kumasuke.royce.util.ResultSets;
import app.kumasuke.royce.util.SingletonContext;
import app.kumasuke.royce.util.TypeConverter;

import javax.annotation.Nonnull;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

class LabelMapResultSetMapper implements ResultSetMapper<Map<String, Object>> {
    private static final SingletonContext<LabelMapResultSetMapper> singleton =
            new SingletonContext<>(LabelMapResultSetMapper::new);

    private LabelMapResultSetMapper() {
    }

    static LabelMapResultSetMapper getInstance() {
        return singleton.getInstance();
    }

    @Override
    public Map<String, Object> mapRow(@Nonnull ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();

        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            Class<?> javaType = getJavaType(metaData, i);
            Object value = ResultSets.getValue(rs, i, javaType);
            result.put(label, value);
        }
        return result;
    }

    private Class<?> getJavaType(ResultSetMetaData metaData, int column) throws SQLException {
        int columnType = metaData.getColumnType(column);
        JDBCType jdbcType = JDBCType.valueOf(columnType);
        return TypeConverter.jdbcTypeToJavaType(jdbcType);
    }
}
