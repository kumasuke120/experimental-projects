package app.kumasuke.royce.linker;

import app.kumasuke.royce.util.SingletonContext;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

class ConnectionHolder {
    private final Connection conn;
    private final SingletonContext<Connection> connectionSingleton;
    private volatile boolean held;

    ConnectionHolder(Connection conn) {
        this.held = true;
        this.conn = conn;
        this.connectionSingleton = new SingletonContext<>(UnclosableConnection::new);
    }

    final void releaseConnection() {
        held = false;
    }

    final Connection getConnection() {
        return connectionSingleton.getInstance();
    }

    private Connection requireConnection() {
        if (!held) {
            throw new IllegalStateException("connection is not held by this linker anymore");
        } else {
            return conn;
        }
    }

    private class UnclosableConnection implements Connection {
        @Override
        public Statement createStatement() throws SQLException {
            return requireConnection().createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return requireConnection().prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return requireConnection().prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return requireConnection().nativeSQL(sql);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return requireConnection().getAutoCommit();
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            requireConnection().setAutoCommit(autoCommit);
        }

        @Override
        public void commit() throws SQLException {
            requireConnection().commit();
        }

        @Override
        public void rollback() throws SQLException {
            requireConnection().rollback();
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return requireConnection().isClosed();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return requireConnection().getMetaData();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return requireConnection().isReadOnly();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            requireConnection().setReadOnly(readOnly);
        }

        @Override
        public String getCatalog() throws SQLException {
            return requireConnection().getCatalog();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            requireConnection().setCatalog(catalog);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return requireConnection().getTransactionIsolation();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            requireConnection().setTransactionIsolation(level);
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return requireConnection().getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            requireConnection().clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return requireConnection().createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return requireConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return requireConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return requireConnection().getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            requireConnection().setTypeMap(map);
        }

        @Override
        public int getHoldability() throws SQLException {
            return requireConnection().getHoldability();
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            requireConnection().setHoldability(holdability);
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return requireConnection().setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return requireConnection().setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            requireConnection().rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            requireConnection().releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return requireConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                                  int resultSetHoldability) throws SQLException {
            return requireConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                             int resultSetHoldability) throws SQLException {
            return requireConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return requireConnection().prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return requireConnection().prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return requireConnection().prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            return requireConnection().createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            return requireConnection().createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return requireConnection().createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return requireConnection().createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return requireConnection().isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            requireConnection().setClientInfo(name, value);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return requireConnection().getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return requireConnection().getClientInfo();
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            requireConnection().setClientInfo(properties);
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return requireConnection().createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return requireConnection().createStruct(typeName, attributes);
        }

        @Override
        public String getSchema() throws SQLException {
            return requireConnection().getSchema();
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            requireConnection().setSchema(schema);
        }

        @Override
        public void abort(Executor executor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            requireConnection().setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return requireConnection().getNetworkTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return requireConnection().unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return requireConnection().isWrapperFor(iface);
        }
    }
}
