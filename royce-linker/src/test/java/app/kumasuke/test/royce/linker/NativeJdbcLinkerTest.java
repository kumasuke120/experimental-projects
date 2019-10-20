package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.linker.Linkers;
import app.kumasuke.royce.linker.NativeJdbcLinker;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class NativeJdbcLinkerTest {
    private static ConnectionProvider provider;
    private NativeJdbcLinker linker;
    private Connection conn;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        conn = provider.getConnection();
        linker = Linkers.nativeJdbc(conn);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    void jdbcConnection() {
        Connection connection = null;
        try {
            connection = linker.jdbcConnection();
            assertNotNull(connection);
        } finally {
            if (connection != null) {
                assertThrows(UnsupportedOperationException.class, connection::close);
            }
        }
    }

    @Test
    void statement() throws SQLException {
        try (Statement statement = linker.statement()) {
            assertNotNull(statement);
        }
    }

    @Test
    void preparedStatement() throws SQLException {
        try (PreparedStatement preparedStatement = linker.preparedStatement("SELECT 1")) {
            assertNotNull(preparedStatement);
        }
    }

    @Test
    void callableStatement() throws SQLException {
        try (CallableStatement callableStatement = linker.callableStatement("{call test()}")) {
            assertNotNull(callableStatement);
        }
    }

    @Test
    void commit() throws SQLException {
        Connection mock = mock(Connection.class);

        final boolean[] calledRef = {false};
        doAnswer(mck -> calledRef[0] = true).when(mock).commit();

        NativeJdbcLinker linker = Linkers.nativeJdbc(mock);
        linker.commit();

        assertTrue(calledRef[0]);
    }

    @Test
    void rollback() throws SQLException {
        Connection mock = mock(Connection.class);

        final boolean[] calledRef = {false};
        doAnswer(mck -> calledRef[0] = true).when(mock).rollback();

        NativeJdbcLinker linker = Linkers.nativeJdbc(mock);
        linker.rollback();

        assertTrue(calledRef[0]);
    }

    @Test
    void databaseMetaData() throws SQLException {
        DatabaseMetaData databaseMetaData = linker.databaseMetaData();
        assertNotNull(databaseMetaData);
    }
}
