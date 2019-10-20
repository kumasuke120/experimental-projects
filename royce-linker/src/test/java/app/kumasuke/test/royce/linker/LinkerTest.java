package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.linker.Linker;
import app.kumasuke.royce.linker.Linkers;
import app.kumasuke.royce.linker.NativeJdbcLinker;
import app.kumasuke.royce.util.CallParameter;
import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinkerTest {
    @Mock
    private Connection mockConn;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void selectOne() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).selectOne(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).selectOne(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).selectOne(rs -> "", "SELECT 1"));
    }

    @Test
    void namedSelectOne() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).namedSelectOne(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).namedSelectOne(rs -> "", "SELECT 1",
                                                                             new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).namedSelectOne(rs -> "", "SELECT 1",
                                                                             new Object()));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).namedSelectOne(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).namedSelectOne(rs -> "", "SELECT 1",
                                                                                 new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).namedSelectOne(rs -> "", "SELECT 1",
                                                                                 new Object()));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).namedSelectOne(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).namedSelectOne(rs -> "", "SELECT 1",
                                                                              new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).namedSelectOne(rs -> "", "SELECT 1",
                                                                              new Object()));
    }

    @Test
    void selectMany() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).selectMany(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).selectMany(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).selectMany(rs -> "", "SELECT 1"));
    }

    @Test
    void namedSelectMany() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).namedSelectMany(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).namedSelectMany(rs -> "", "SELECT 1",
                                                                              new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readable(mockConn)).namedSelectMany(rs -> "", "SELECT 1",
                                                                              new Object()));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).namedSelectMany(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).namedSelectMany(rs -> "", "SELECT 1",
                                                                                  new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).namedSelectMany(rs -> "", "SELECT 1",
                                                                                  new Object()));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).namedSelectMany(rs -> "", "SELECT 1"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).namedSelectMany(rs -> "", "SELECT 1",
                                                                               new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).namedSelectMany(rs -> "", "SELECT 1",
                                                                               new Object()));
    }

    @Test
    void update() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.writable(mockConn)).update("UPDATE test.books SET id = id"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn)).update("UPDATE test.books SET id = id"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).update("UPDATE test.books SET id = id"));
    }

    @Test
    void namedUpdate() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.writable(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.writable(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id", new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.writable(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id", new Object()));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id", new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id", new Object()));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id", new HashMap<>()));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedUpdate("UPDATE test.books SET id = id", new Object()));
    }

    @Test
    void batchUpdate() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.writable(mockConn))
                             .batchUpdate("UPDATE test.books SET id = id").performUpdate());
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn))
                             .batchUpdate("UPDATE test.books SET id = id").performUpdate());
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .batchUpdate("UPDATE test.books SET id = id").performUpdate());
    }

    @Test
    void namedBatchUpdate() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.writable(mockConn))
                             .namedBatchUpdate("UPDATE test.books SET id = id").performUpdate());
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.readWritable(mockConn))
                             .namedBatchUpdate("UPDATE test.books SET id = id").performUpdate());
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedBatchUpdate("UPDATE test.books SET id = id").performUpdate());
    }

    @Test
    void call() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn)).call("{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn)).call("{call test.seq_make('fake_seq')}"));
    }

    @Test
    void namedCall() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .namedCall("{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .namedCall("{call test.seq_make(:name)}",
                                        Collections.singletonMap("name", CallParameter.in("fake_seq"))));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedCall("{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedCall("{call test.seq_make(:name)}",
                                        Collections.singletonMap("name", CallParameter.in("fake_seq"))));
    }

    @Test
    void callAndReturnOne() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .callAndReturnOne(rs -> null, "{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .callAndReturnOne(rs -> null, "{call test.seq_make('fake_seq')}"));
    }

    @Test
    void namedCallAndReturnOne() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .namedCallAndReturnOne(rs -> null, "{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .namedCallAndReturnOne(rs -> null, "{call test.seq_make(:name)}",
                                                    Collections.singletonMap("name", CallParameter.in("fake_seq"))));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedCallAndReturnOne(rs -> null, "{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedCallAndReturnOne(rs -> null, "{call test.seq_make(:name)}",
                                                    Collections.singletonMap("name", CallParameter.in("fake_seq"))));
    }

    @Test
    void callAndReturnMany() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .callAndReturnMany(rs -> null, "{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .callAndReturnMany(rs -> null, "{call test.seq_make('fake_seq')}"));
    }

    @Test
    void namedCallAndReturnMany() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .namedCallAndReturnMany(rs -> null, "{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.callable(mockConn))
                             .namedCallAndReturnMany(rs -> null, "{call test.seq_make(:name)}",
                                                     Collections.singletonMap("name", CallParameter.in("fake_seq"))));

        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedCallAndReturnMany(rs -> null, "{call test.seq_make('fake_seq')}"));
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.universal(mockConn))
                             .namedCallAndReturnMany(rs -> null, "{call test.seq_make(:name)}",
                                                     Collections.singletonMap("name", CallParameter.in("fake_seq"))));
    }

    @Test
    void jdbcConnection() {
        final NativeJdbcLinker linker = closed(Linkers.nativeJdbc(mockConn));
        Connection jdbcConn = linker.jdbcConnection();
        Reflects.getPublicInstanceMethods(jdbcConn)
                .forEach(m -> {
                    try {
                        Reflects.invokeWithDefaultArguments(m, jdbcConn);
                    } catch (InvocationTargetException e) {
                        if (Arrays.asList("close", "abort").contains(m.getName())) {
                            assertEquals(UnsupportedOperationException.class, e.getTargetException().getClass());
                        } else {
                            assertEquals(IllegalStateException.class, e.getTargetException().getClass());
                        }
                    }
                });
    }

    @Test
    void statement() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.nativeJdbc(mockConn))
                             .statement());
    }

    @Test
    void preparedStatement() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.nativeJdbc(mockConn))
                             .preparedStatement("SELECT 1"));
    }

    @Test
    void callableStatement() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.nativeJdbc(mockConn))
                             .callableStatement("{call test()}"));
    }

    @Test
    void commit() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.nativeJdbc(mockConn))
                             .commit());
    }

    @Test
    void rollback() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.nativeJdbc(mockConn))
                             .rollback());
    }

    @Test
    void databaseMetaData() {
        assertThrows(IllegalStateException.class,
                     () -> closed(Linkers.nativeJdbc(mockConn))
                             .databaseMetaData());
    }

    private <L extends Linker> L closed(L linker) {
        linker.close();
        return linker;
    }
}
