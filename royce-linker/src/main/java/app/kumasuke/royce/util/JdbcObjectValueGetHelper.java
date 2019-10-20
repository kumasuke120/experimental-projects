package app.kumasuke.royce.util;

import javax.annotation.Nonnull;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class JdbcObjectValueGetHelper {
    private JdbcObjectValueGetHelper() {
        throw new UnsupportedOperationException();
    }

    static Object getValue(@Nonnull ValueGettable valueGettable, int index, @Nonnull Class<?> javaType)
            throws SQLException {
        if (byte.class.equals(javaType)) {
            return valueGettable.getByte(index);
        } else if (Byte.class.equals(javaType)) {
            Object value = valueGettable.getObject(index);
            if (value == null) {
                return null;
            } else if (value instanceof Number) {
                return ((Number) value).byteValue();
            }
            // unable to converted to byte, jump to the last line to trigger raw SQLException
        } else if (short.class.equals(javaType)) {
            return valueGettable.getShort(index);
        } else if (Short.class.equals(javaType)) {
            Object value = valueGettable.getObject(index);
            if (value == null) {
                return null;
            } else if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            // unable to converted to short, jump to the last line to trigger raw SQLException
        } else if (Void.class.equals(javaType)) {
            return valueGettable.getObject(index);
        }

        return valueGettable.getObject(index, javaType);
    }

    interface ValueGettable {
        byte getByte(int columnIndex) throws SQLException;

        short getShort(int columnIndex) throws SQLException;

        Object getObject(int columnIndex) throws SQLException;

        <T> T getObject(int columnIndex, Class<T> type) throws SQLException;
    }

    static class ResultSetValueGetSupport implements ValueGettable {
        private final ResultSet resultSet;

        ResultSetValueGetSupport(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public byte getByte(int columnIndex) throws SQLException {
            return resultSet.getByte(columnIndex);
        }

        @Override
        public short getShort(int columnIndex) throws SQLException {
            return resultSet.getShort(columnIndex);
        }

        @Override
        public Object getObject(int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex);
        }

        @Override
        public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            return resultSet.getObject(columnIndex, type);
        }
    }

    static class CallableStatementValueGetSupport implements ValueGettable {
        private final CallableStatement callableStatement;

        CallableStatementValueGetSupport(CallableStatement callableStatement) {
            this.callableStatement = callableStatement;
        }

        @Override
        public byte getByte(int columnIndex) throws SQLException {
            return callableStatement.getByte(columnIndex);
        }

        @Override
        public short getShort(int columnIndex) throws SQLException {
            return callableStatement.getShort(columnIndex);
        }

        @Override
        public Object getObject(int columnIndex) throws SQLException {
            return callableStatement.getObject(columnIndex);
        }

        @Override
        public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            return callableStatement.getObject(columnIndex, type);
        }
    }
}
