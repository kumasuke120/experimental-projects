package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.linker.*;
import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.CallParameter;
import app.kumasuke.test.util.Reflects;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniversalLinkerTest {
    private static ConnectionProvider provider;
    private UniversalLinker linker;
    private Connection conn;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        conn = provider.getConnection();
        linker = Linkers.universal(conn);
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
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.selectOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.selectOne(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedSelectOne() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.namedSelectOne(any(), anyString())).thenCallRealMethod();
        when(readWritableLinker.namedSelectOne(any(), anyString(), any()))
                .thenAnswer(getNamedSelectAnswer(invokedRef, 2));
        when(readWritableLinker.namedSelectOne(any(), anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.namedSelectOne(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(readWritableLinker.namedSelectOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("test2", "test2"), parameters);
            return null;
        });
        linker.namedSelectOne(rs -> null, "SELECT 1", Collections.singletonMap("test2", "test2"));
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        linker.namedSelectOne(rs -> null, "SELECT 1", new Object());
        assertTrue(invokedRef[0]);
    }

    @Test
    void selectMany() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.selectMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.selectMany(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedSelectMany() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.namedSelectMany(any(), anyString())).thenCallRealMethod();
        when(readWritableLinker.namedSelectMany(any(), anyString(), any()))
                .thenAnswer(getNamedSelectAnswer(invokedRef, 2));
        when(readWritableLinker.namedSelectMany(any(), anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.namedSelectMany(rs -> null, "SELECT 1");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(readWritableLinker.namedSelectMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("test2", "test2"), parameters);
            return null;
        });
        linker.namedSelectMany(rs -> null, "SELECT 1", Collections.singletonMap("test2", "test2"));
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        linker.namedSelectMany(rs -> null, "SELECT 1", new Object());
        assertTrue(invokedRef[0]);
    }

    @Test
    void update() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.update(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return 0;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.update("UPDATE test.books SET id = 8 WHERE id = 88");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedUpdate() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.namedUpdate(anyString())).thenCallRealMethod();
        when(readWritableLinker.namedUpdate(anyString(), any()))
                .thenAnswer(getNamedSelectAnswer(invokedRef, 1));
        when(readWritableLinker.namedUpdate(anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.namedUpdate("UPDATE test.books SET id = 8 WHERE id = 188");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(readWritableLinker.namedUpdate(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(1);
            assertEquals(Collections.singletonMap("id", 188), parameters);
            return null;
        });
        linker.namedUpdate("UPDATE test.books SET id = 8 WHERE id = :id",
                           Collections.singletonMap("id", 188));
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        linker.namedUpdate("UPDATE test.books SET id = 8 WHERE id = 188", new Object());
        assertTrue(invokedRef[0]);
    }

    @Test
    void updateAndReturnKey() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.updateAndReturnKey(any(), anyString(), (Object) any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            ResultSetMapper keyMapper = mck.getArgument(0);
            return Optional.ofNullable(keyMapper.mapRow(null));
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        int retVal = linker.updateAndReturnKey(rs -> 1,
                                               "INSERT INTO test.auto_increment_id(`value`) VALUES (?)",
                                               "A")
                .orElseThrow(AssertionError::new);
        assertTrue(invokedRef[0]);
        assertEquals(1, retVal);
    }

    @Test
    void namedUpdateAndReturnKey() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.namedUpdateAndReturnKey(any(), anyString())).thenCallRealMethod();
        when(readWritableLinker.namedUpdateAndReturnKey(any(), anyString(), any()))
                .thenAnswer(mck -> {
                    invokedRef[0] = true;
                    Map<String, Object> parameters = mck.getArgument(2);
                    assertNull(parameters);
                    ResultSetMapper keyMapper = mck.getArgument(0);
                    return keyMapper == null ? null : Optional.ofNullable(keyMapper.mapRow(null));
                });
        when(readWritableLinker.namedUpdateAndReturnKey(any(), anyString(), (Object) any()))
                .thenAnswer(mck -> {
                    invokedRef[0] = true;
                    ResultSetMapper keyMapper = mck.getArgument(0);
                    return Optional.ofNullable(keyMapper.mapRow(null));
                });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        int retVal = linker
                .namedUpdateAndReturnKey(rs -> 1,
                                         "INSERT INTO test.auto_increment_id(`value`) VALUES ('A')")
                .orElseThrow(AssertionError::new);
        assertTrue(invokedRef[0]);
        assertEquals(1, retVal);

        invokedRef[0] = false;
        when(readWritableLinker.namedUpdateAndReturnKey(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("value", "A"), parameters);
            ResultSetMapper keyMapper = mck.getArgument(0);
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
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.batchUpdate(anyString())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.batchUpdate("UPDATE test.books SET `language` = ? WHERE id = ?");
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedBatchUpdate() {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        when(readWritableLinker.namedBatchUpdate(anyString())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return null;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        linker.namedBatchUpdate("UPDATE test.books SET `language` = :lang WHERE id = :id");
        assertTrue(invokedRef[0]);
    }

    @Test
    void batchExecute() {
        final boolean[] invokedRef = {false};
        ReadWritableLinker readWritableLinker = mock(ReadWritableLinker.class);
        BatchExecuteHelper batchExecuteHelper = mock(BatchExecuteHelper.class);
        when(readWritableLinker.batchExecute()).thenAnswer(mck -> {
            invokedRef[0] = true;
            return batchExecuteHelper;
        });
        Reflects.setField(linker, "rwLinkerImpl", readWritableLinker);

        BatchExecuteHelper retVal = linker.batchExecute();
        assertTrue(invokedRef[0]);
        assertSame(batchExecuteHelper, retVal);
    }

    @Test
    void call() throws SQLException {
        final boolean[] invokedRef = {false};
        CallableLinker callableLinker = mock(CallableLinker.class);
        when(callableLinker.call(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return 0;
        });
        Reflects.setField(linker, "cLinkerImpl", callableLinker);

        linker.call("{call test.seq_make(?)}", CallParameter.in("fake_seq"));
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedCall() throws SQLException {
        final boolean[] invokedRef = {false};
        CallableLinker callableLinker = mock(CallableLinker.class);
        when(callableLinker.namedCall(anyString())).thenCallRealMethod();
        when(callableLinker.namedCall(anyString(), any()))
                .thenAnswer(getNamedSelectAnswer(invokedRef, 1));
        Reflects.setField(linker, "cLinkerImpl", callableLinker);

        linker.namedCall("{call test.seq_make(:name)}");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(callableLinker.namedCall(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(1);
            assertEquals(Collections.singletonMap("name", CallParameter.in("fake_seq")), parameters);
            return 0;
        });
        linker.namedCall("{call test.seq_make(:name)}",
                         Collections.singletonMap("name", CallParameter.in("fake_seq")));
        assertTrue(invokedRef[0]);
    }

    @Test
    void callAndReturnOne() throws SQLException {
        final boolean[] invokedRef = {false};
        CallableLinker callableLinker = mock(CallableLinker.class);
        when(callableLinker.callAndReturnOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return Optional.of(0);
        });
        Reflects.setField(linker, "cLinkerImpl", callableLinker);

        linker.callAndReturnOne(rs -> null, "{call test.get_books_by_language(?)}",
                                CallParameter.in("English"));
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedCallAndReturnOne() throws SQLException {
        final boolean[] invokedRef = {false};
        CallableLinker callableLinker = mock(CallableLinker.class);
        when(callableLinker.namedCallAndReturnOne(any(), anyString())).thenCallRealMethod();
        when(callableLinker.namedCallAndReturnOne(any(), anyString(), any()))
                .thenAnswer(getNamedSelectAnswer(invokedRef, 2));
        Reflects.setField(linker, "cLinkerImpl", callableLinker);

        linker.namedCallAndReturnOne(rs -> null, "{call test.get_books_by_id(:id)}");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(callableLinker.namedCallAndReturnOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("id", CallParameter.in(1)), parameters);
            return Optional.of(0);
        });
        linker.namedCallAndReturnOne(rs -> null, "{call test.get_books_by_id(:id)}",
                                     Collections.singletonMap("id", CallParameter.in(1)));
        assertTrue(invokedRef[0]);
    }

    @Test
    void callAndReturnMany() throws SQLException {
        final boolean[] invokedRef = {false};
        CallableLinker callableLinker = mock(CallableLinker.class);
        when(callableLinker.callAndReturnMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            return Stream.of(Collections.singleton(1));
        });
        Reflects.setField(linker, "cLinkerImpl", callableLinker);

        linker.callAndReturnMany(rs -> null, "{call test.get_books_by_language(?)}",
                                 CallParameter.in("English"));
        assertTrue(invokedRef[0]);
    }

    @Test
    void namedCallAndReturnMany() throws SQLException {
        final boolean[] invokedRef = {false};
        CallableLinker callableLinker = mock(CallableLinker.class);
        when(callableLinker.namedCallAndReturnMany(any(), anyString())).thenCallRealMethod();
        when(callableLinker.namedCallAndReturnMany(any(), anyString(), any()))
                .thenAnswer(getNamedSelectAnswer(invokedRef, 2));
        Reflects.setField(linker, "cLinkerImpl", callableLinker);

        linker.namedCallAndReturnMany(rs -> null, "{call test.get_books_by_language(:lang)}");
        assertTrue(invokedRef[0]);

        invokedRef[0] = false;
        when(callableLinker.namedCallAndReturnMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(2);
            assertEquals(Collections.singletonMap("lang", CallParameter.in("English")), parameters);
            return 0;
        });
        linker.namedCallAndReturnOne(rs -> null, "{call test.get_books_by_language(:lang)}",
                                     Collections.singletonMap("lang", CallParameter.in("English")));
        assertTrue(invokedRef[0]);
    }

    private Answer<Object> getNamedSelectAnswer(boolean[] invokedRef, int index) {
        return mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(index);
            assertNull(parameters);
            return null;
        };
    }
}
