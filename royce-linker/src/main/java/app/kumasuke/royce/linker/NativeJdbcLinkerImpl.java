package app.kumasuke.royce.linker;

import java.sql.*;

class NativeJdbcLinkerImpl extends ConnectionHolder implements NativeJdbcLinker {
    NativeJdbcLinkerImpl(Connection conn) {
        super(conn);
    }

    @Override
    public void close() {
        super.releaseConnection();
    }

    @Override
    public Connection jdbcConnection() {
        return getConnection();
    }

    @Override
    public Statement statement() throws SQLException {
        return getConnection().createStatement();
    }

    @Override
    public PreparedStatement preparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement callableStatement(String sql) throws SQLException {
        return getConnection().prepareCall(sql);
    }

    @Override
    public void commit() throws SQLException {
        getConnection().commit();
    }

    @Override
    public void rollback() throws SQLException {
        getConnection().rollback();
    }

    @Override
    public DatabaseMetaData databaseMetaData() throws SQLException {
        return getConnection().getMetaData();
    }
}
