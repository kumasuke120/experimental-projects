package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.linker.*;
import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.test.util.Reflects;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadWritableLinkerTest {
    private static ConnectionProvider provider;
    private ReadWritableLinker linker;
    private Connection conn;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        conn = provider.getConnection();
        linker = Linkers.readWritable(conn);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    void selectOne() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadableLinker readableLinker = mock(ReadableLinker.class);
        when(readableLinker.selectOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rLinkerImpl", readableLinker);

        linker.selectOne(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedSelectOne() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadableLinker readableLinker = mock(ReadableLinker.class);
        when(readableLinker.namedSelectOne(any(), anyString())).thenCallRealMethod();
        when(readableLinker.namedSelectOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertNull(parameters);
            return null;
        });
        when(readableLinker.namedSelectOne(any(), anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rLinkerImpl", readableLinker);

        linker.namedSelectOne(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(readableLinker.namedSelectOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("test", "test"), parameters);
            return null;
        });
        linker.namedSelectOne(rs -> null, "SELECT 1", Collections.singletonMap("test", "test"));
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        linker.namedSelectOne(rs -> null, "SELECT 1", new Object());
        assertTrue(invokedRef[0]);
    }

    @Test
    void selectMany() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadableLinker readableLinker = mock(ReadableLinker.class);
        when(readableLinker.selectMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rLinkerImpl", readableLinker);

        linker.selectMany(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedSelectMany() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadableLinker readableLinker = mock(ReadableLinker.class);
        when(readableLinker.namedSelectMany(any(), anyString())).thenCallRealMethod();
        when(readableLinker.namedSelectMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertNull(parameters);
            return null;
        });
        when(readableLinker.namedSelectMany(any(), anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rLinkerImpl", readableLinker);

        linker.namedSelectMany(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(readableLinker.namedSelectMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("test", "test"), parameters);
            return null;
        });
        linker.namedSelectMany(rs -> null, "SELECT 1", Collections.singletonMap("test", "test"));
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        linker.namedSelectMany(rs -> null, "SELECT 1", new Object());
        assertTrue(invokedRef[0]);
    }

    @Test
    void update() throws SQLException {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        when(writableLinker.update(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return 0;
        });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        linker.update("UPDATE test.books SET id = 8 WHERE id = 88");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedUpdate() throws SQLException {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        when(writableLinker.namedUpdate(anyString())).thenCallRealMethod();
        when(writableLinker.namedUpdate(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(1);
            assertNull(parameters);
            return null;
        });
        when(writableLinker.namedUpdate(anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        linker.namedUpdate("UPDATE test.books SET id = 8 WHERE id = 88");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(writableLinker.namedUpdate(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(1);
            assertEquals(Collections.singletonMap("id", 88), parameters);
            return null;
        });
        linker.namedUpdate("UPDATE test.books SET id = 8 WHERE id = :id",
                           Collections.singletonMap("id", 88));
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        linker.namedUpdate("UPDATE test.books SET id = 8 WHERE id = 88", new Object());
        assertTrue(invokedRef[0]);
    }

    @Test
    void updateAndReturnKey() throws SQLException {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        when(writableLinker.updateAndReturnKey(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            ResultSetMapper<?> keyMapper = mck.getArgument(0);
            assertNotNull(keyMapper);
            assertNotNull(mck.getArgument(1));
            assertNotNull(mck.getArgument(2));
            return Optional.ofNullable(keyMapper.mapRow(null));
        });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        Optional<Integer> id =
                linker.updateAndReturnKey(rs -> 0,
                                          "INSERT test.auto_increment_id(`value`)  VALUES (?)",
                                          "A");
        assertEquals(0, id.orElseThrow(AssertionError::new).intValue());
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedUpdateAndReturnKey() throws SQLException {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        when(writableLinker.namedUpdateAndReturnKey(any(), anyString())).thenCallRealMethod();
        when(writableLinker.namedUpdateAndReturnKey(any(), anyString(), any()))
                .thenAnswer(mck -> {
                    invokedRef[0] = true;
                    Map<String, Object> parameters = mck.getArgument(2);
                    ResultSetMapper keyMapper = mck.getArgument(0);
                    assertNull(parameters);
                    return keyMapper == null ? null : Optional.ofNullable(keyMapper.mapRow(null));
                });
        when(writableLinker.namedUpdateAndReturnKey(any(), anyString(), (Object) any()))
                .thenAnswer(mck -> {
                    invokedRef[0] = true;
                    ResultSetMapper keyMapper = mck.getArgument(0);
                    return Optional.ofNullable(keyMapper.mapRow(null));
                });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        int retVal = linker
                .namedUpdateAndReturnKey(rs -> 1,
                                         "INSERT INTO test.auto_increment_id(`value`) VALUES ('A')")
                .orElseThrow(AssertionError::new);
        assertTrue(invokedRef[0]);
        assertEquals(1, retVal);

        invokedRef[0] = false;
        when(writableLinker.namedUpdateAndReturnKey(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            ResultSetMapper keyMapper = mck.getArgument(0);
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("value", "A"), parameters);
            return Optional.ofNullable(keyMapper.mapRow(null));
        });
        int retVal2 = linker
                .namedUpdateAndReturnKey(rs -> 2,
                                         "INSERT INTO test.auto_increment_id(`value`) VALUES (:value)",
                                         Collections.singletonMap("value", "A"))
                .orElseThrow(AssertionError::new);
        assertTrue(invokedRef[0]);
        assertEquals(2, retVal2);

        invokedRef[0] = false;
        int retVal3 = linker
                .namedUpdateAndReturnKey(rs -> 3,
                                         "INSERT INTO test.auto_increment_id(`value`) VALUES ('A')",
                                         new Object())
                .orElseThrow(AssertionError::new);
        assertTrue(invokedRef[0]);
        assertEquals(3, retVal3);
    }

    @Test
    void batchUpdate() {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        when(writableLinker.batchUpdate(anyString())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        linker.batchUpdate("UPDATE test.books SET `language` = ? WHERE id = ?");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedBatchUpdate() {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        when(writableLinker.namedBatchUpdate(anyString())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        linker.namedBatchUpdate("UPDATE test.books SET `language` = :lang WHERE id = :id");
        assertTrue(invokedRef[0]);
    }

    @Test
    void batchExecute() {
        final boolean[] invokedRef = {false};
        WritableLinker writableLinker = mock(WritableLinker.class);
        BatchExecuteHelper batchExecuteHelper = mock(BatchExecuteHelper.class);
        when(writableLinker.batchExecute()).thenAnswer(mck -> {
            invokedRef[0] = true;
            return batchExecuteHelper;
        });
        Reflects.setField(linker, "wLinkerImpl", writableLinker);

        BatchExecuteHelper retVal = linker.batchExecute();
        assertTrue(invokedRef[0]);
        assertSame(batchExecuteHelper, retVal);
    }
}
