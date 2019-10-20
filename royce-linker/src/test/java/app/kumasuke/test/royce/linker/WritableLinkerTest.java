package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.except.UncheckedSQLException;
import app.kumasuke.royce.linker.Linkers;
import app.kumasuke.royce.linker.NamedBatchUpdateHelper;
import app.kumasuke.royce.linker.WritableLinker;
import app.kumasuke.test.util.Book;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("SameReturnValue")
class WritableLinkerTest {
    private static ConnectionProvider provider;
    private WritableLinker linker;
    private Connection conn;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        conn = provider.getConnection();
        linker = Linkers.writable(conn);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    void update() throws SQLException {
        final LocalDate newDate = LocalDate.of(2008, 8, 8);
        int retVal = linker.update("UPDATE test.books SET release_date = ? WHERE id = 8", newDate);
        assertEquals(1, retVal);
        assertQuery("SELECT release_date FROM test.books WHERE id = 8", rs -> {
            assertTrue(rs.next());
            LocalDate date = rs.getObject(1, LocalDate.class);
            assertEquals(newDate, date);
        });

        final LocalDate oldDate = LocalDate.of(2005, 6, 1);
        int retVal2 = linker.update("UPDATE test.books SET release_date = ?, pages = ? WHERE id = 8",
                                    oldDate, 334);
        assertEquals(1, retVal2);
        assertQuery("SELECT release_date FROM test.books WHERE id = 8", rs -> {
            assertTrue(rs.next());
            LocalDate date = rs.getObject(1, LocalDate.class);
            assertEquals(oldDate, date);
        });
    }

    @Test
    void namedUpdate() throws SQLException {
        final boolean[] invokedRef = {false};
        WritableLinker mock = mock(WritableLinker.class);
        when(mock.namedUpdate(anyString())).thenCallRealMethod();
        when(mock.namedUpdate(anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            Map<String, Object> parameters = mck.getArgument(1);
            assertNull(parameters);
            return 1;
        });
        int retVal = mock.namedUpdate("UPDATE test.books SET id = 8 WHERE id = 88");
        assertTrue(invokedRef[0]);
        assertEquals(1, retVal);

        final LocalDate newDate = LocalDate.of(2008, 8, 8);
        int retVal2 = linker.namedUpdate("UPDATE test.books SET release_date = :rd WHERE id = 8",
                                         Collections.singletonMap("rd", newDate));
        assertEquals(1, retVal2);
        assertQuery("SELECT release_date FROM test.books WHERE id = 8", rs -> {
            assertTrue(rs.next());
            LocalDate date = rs.getObject(1, LocalDate.class);
            assertEquals(newDate, date);
        });

        final LocalDate oldDate = LocalDate.of(2005, 6, 1);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rd", oldDate);
        parameters.put("p", 334);
        int retVal3 = linker.namedUpdate("UPDATE test.books SET release_date = :rd, pages = :p WHERE id = 8",
                                         parameters);
        assertEquals(1, retVal3);
        assertQuery("SELECT release_date FROM test.books WHERE id = 8", rs -> {
            assertTrue(rs.next());
            LocalDate date = rs.getObject(1, LocalDate.class);
            assertEquals(oldDate, date);
        });

        Book book = new Book();
        book.setId(6);
        book.setLanguage("中文");
        int retVal4 = linker.namedUpdate("UPDATE test.books SET `language` = :language WHERE id = :id",
                                         book);
        assertEquals(1, retVal4);
        assertQuery("SELECT `language` FROM test.books WHERE id = 6", rs -> {
            assertTrue(rs.next());
            String language = rs.getString(1);
            assertEquals(book.getLanguage(), language);
        });

        book.setLanguage("English");
        int retVal5 = linker.namedUpdate("UPDATE test.books SET `language` = :language WHERE id = :id",
                                         book);
        assertEquals(1, retVal5);
        assertQuery("SELECT `language` FROM test.books WHERE id = 6", rs -> {
            assertTrue(rs.next());
            String language = rs.getString(1);
            assertEquals(book.getLanguage(), language);
        });
    }

    @Test
    void updateAndReturnKey() throws SQLException {
        try {
            int retVal =
                    linker.updateAndReturnKey(rs -> rs.getInt(1),
                                              "INSERT INTO test.auto_increment_id(`value`) VALUES (?)",
                                              "B")
                            .orElseThrow(AssertionError::new);
            assertEquals(1, retVal);

            int retVal2 =
                    linker.updateAndReturnKey(rs -> rs.getInt(1),
                                              "INSERT INTO test.auto_increment_id(`value`) VALUES (?)",
                                              "B")
                            .orElseThrow(AssertionError::new);
            assertEquals(2, retVal2);
        } finally {
            linker.update("TRUNCATE TABLE test.auto_increment_id");
            linker.update("ALTER TABLE test.auto_increment_id AUTO_INCREMENT = 1");
        }
    }

    @Test
    void namedUpdateAndReturnKey() throws SQLException {
        try {
            int retVal =
                    linker.namedUpdateAndReturnKey(rs -> rs.getInt(1),
                                                   "INSERT INTO test.auto_increment_id(`value`) " +
                                                           "VALUES (:value)",
                                                   Collections.singletonMap("value", "A"))
                            .orElseThrow(AssertionError::new);
            assertEquals(1, retVal);

            int retVal2 =
                    linker.namedUpdateAndReturnKey(rs -> rs.getInt(1),
                                                   "INSERT INTO test.auto_increment_id(`value`) " +
                                                           "VALUES (:value)",
                                                   new Object() {
                                                       @SuppressWarnings("unused")
                                                       public String getValue() {
                                                           return "B";
                                                       }
                                                   })
                            .orElseThrow(AssertionError::new);
            assertEquals(2, retVal2);

            int retVal3 =
                    linker.namedUpdateAndReturnKey(rs -> rs.getInt(1),
                                                   "INSERT INTO test.auto_increment_id(`value`) " +
                                                           "VALUES ('C')")
                            .orElseThrow(AssertionError::new);
            assertEquals(3, retVal3);
        } finally {
            linker.update("TRUNCATE TABLE test.auto_increment_id");
            linker.update("ALTER TABLE test.auto_increment_id AUTO_INCREMENT = 1");
        }
    }

    @Test
    void batchUpdate() throws SQLException {
        int[] retVals = linker.batchUpdate("UPDATE test.books SET `language` = ? WHERE id = ?")
                .addParameters("Chinese", 1)
                .addParameters("Chinese", 2)
                .addParameters("Chinese", 3)
                .addParameters("Chinese", 4)
                .addParameters("Chinese", 5)
                .addParameters("Chinese", 6)
                .performUpdate();
        Arrays.stream(retVals)
                .forEach(t -> assertEquals(1, t));

        for (int i = 0; i < 6; i++) {
            assertQuery("SELECT `language` FROM test.books WHERE id = " + (i + 1), rs -> {
                assertTrue(rs.next());
                assertEquals("Chinese", rs.getString(1));
            });
        }

        int[] retVals2 = linker.batchUpdate("UPDATE test.books SET `language` = ? WHERE id = ?")
                .addParameters("English", 1)
                .addParameters("English", 2)
                .addParameters("English", 3)
                .addParameters("English", 4)
                .addParameters("English", 5)
                .addParameters("English", 6)
                .performUpdate();
        Arrays.stream(retVals2)
                .forEach(t -> assertEquals(1, t));

        for (int i = 0; i < 6; i++) {
            assertQuery("SELECT `language` FROM test.books WHERE id = " + (i + 1), rs -> {
                assertTrue(rs.next());
                assertEquals("English", rs.getString(1));
            });
        }
    }

    @Test
    void batchUpdateAndReturnKeys() throws SQLException {
        try {
            int[] ids = linker.batchUpdate("INSERT INTO test.auto_increment_id(`value`) VALUES (?)")
                    .addParameters("A")
                    .addParameters("B")
                    .addParameters("C")
                    .performUpdateAndReturnKeys(rs -> rs.getInt(1))
                    .mapToInt(i -> i).toArray();
            assertArrayEquals(new int[]{1, 2, 3}, ids);
        } finally {
            linker.update("TRUNCATE TABLE test.auto_increment_id");
            linker.update("ALTER TABLE test.auto_increment_id AUTO_INCREMENT = 1");
        }
    }

    @Test
    void namedBatchUpdate() throws SQLException {
        final boolean[] invokedRef = {false};
        final NamedBatchUpdateHelper mockHelper = mock(NamedBatchUpdateHelper.class);
        when(mockHelper.addParameters()).thenCallRealMethod();
        when(mockHelper.addParameters(any())).thenAnswer(mck -> {
            Object argument = mck.getArgument(0);
            assertNull(argument);
            invokedRef[0] = true;
            return mockHelper;
        });
        final WritableLinker mockLinker = mock(WritableLinker.class);
        when(mockLinker.namedBatchUpdate(anyString())).thenReturn(mockHelper);

        mockLinker.namedBatchUpdate("UPDATE test.books SET `language` = 'Chinese' WHERE id = 8")
                .addParameters();
        assertTrue(invokedRef[0]);

        NamedBatchUpdateHelper helper = linker
                .namedBatchUpdate("UPDATE test.books SET `language` = :lang WHERE id = :id");
        for (int i = 0; i < 6; i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("lang", "Chinese");
            params.put("id", i + 1);
            helper.addParameters(params);
        }
        int[] retVals = helper.performUpdate();
        Arrays.stream(retVals)
                .forEach(t -> assertEquals(1, t));

        for (int i = 0; i < 6; i++) {
            assertQuery("SELECT `language` FROM test.books WHERE id = " + (i + 1), rs -> {
                assertTrue(rs.next());
                assertEquals("Chinese", rs.getString(1));
            });
        }

        NamedBatchUpdateHelper helper2 = linker
                .namedBatchUpdate("UPDATE test.books SET `language` = :lang WHERE id = :id");
        for (int i = 0; i < 6; i++) {
            BatchParametersBean bean = new BatchParametersBean();
            bean.setId(i + 1);
            NamedBatchUpdateHelper retHelper = helper2.addParameters(bean);
            assertSame(retHelper, helper2);
        }
        int[] retVals2 = helper2.performUpdate();
        Arrays.stream(retVals2)
                .forEach(t -> assertEquals(1, t));

        for (int i = 0; i < 6; i++) {
            assertQuery("SELECT `language` FROM test.books WHERE id = " + (i + 1), rs -> {
                assertTrue(rs.next());
                assertEquals("English", rs.getString(1));
            });
        }
    }

    @Test
    void namedBatchUpdateAndReturnKeys() throws SQLException {
        try {
            int[] ids = linker.namedBatchUpdate("INSERT INTO test.auto_increment_id(`value`) " +
                                                        "VALUES (:value)")
                    .addParameters(Collections.singletonMap("value", "A"))
                    .addParameters(Collections.singletonMap("value", "B"))
                    .addParameters(Collections.singletonMap("value", "C"))
                    .performUpdateAndReturnKeys(rs -> rs.getInt(1))
                    .mapToInt(i -> i).toArray();
            assertArrayEquals(new int[]{1, 2, 3}, ids);

            class ParamBean {
                private final String value;

                private ParamBean(String value) {
                    this.value = value;
                }

                @SuppressWarnings("unused")
                public String getValue() {
                    return value;
                }
            }
            int[] ids2 = linker.namedBatchUpdate("INSERT INTO test.auto_increment_id(`value`) " +
                                                         "VALUES (:value)")
                    .addParameters(new ParamBean("D"))
                    .addParameters(new ParamBean("E"))
                    .addParameters(new ParamBean("F"))
                    .performUpdateAndReturnKeys(rs -> rs.getInt(1))
                    .mapToInt(i -> i).toArray();
            assertArrayEquals(new int[]{4, 5, 6}, ids2);
        } finally {
            linker.update("TRUNCATE TABLE test.auto_increment_id");
            linker.update("ALTER TABLE test.auto_increment_id AUTO_INCREMENT = 1");
        }
    }

    @Test
    void batchExecute() throws SQLException {
        final List<String> sqls = Arrays
                .asList("UPDATE test.books SET `name` = 'I Don\\'t Know' WHERE id = 4",
                        "UPDATE test.books SET `name` = 'Steve Jobs' WHERE id = 4");
        linker.batchExecute()
                .add(sqls.get(0))
                .add(sqls.get(1))
                .performUpdate();
        assertQuery("SELECT `name` FROM test.books WHERE id = 4", rs -> {
            assertTrue(rs.next());
            assertEquals("Steve Jobs", rs.getString(1));
        });

        linker.batchExecute()
                .addAll(sqls.toArray(new String[0]))
                .performUpdate();
        assertQuery("SELECT `name` FROM test.books WHERE id = 4", rs -> {
            assertTrue(rs.next());
            assertEquals("Steve Jobs", rs.getString(1));
        });

        linker.batchExecute()
                .addAll(sqls)
                .performUpdate();
        assertQuery("SELECT `name` FROM test.books WHERE id = 4", rs -> {
            assertTrue(rs.next());
            assertEquals("Steve Jobs", rs.getString(1));
        });
    }

    @Test
    void batchExecuteAndReturnKeys() throws SQLException {
        try {
            int[] ids = linker.batchExecute()
                    .add("INSERT INTO test.auto_increment_id(`value`) VALUES ('A')")
                    .add("INSERT INTO test.auto_increment_id(`value`) VALUES ('B')")
                    .add("INSERT INTO test.auto_increment_id(`value`) VALUES ('C')")
                    .performUpdateAndReturnKeys(rs -> rs.getInt(1))
                    .mapToInt(i -> i).toArray();
            assertArrayEquals(new int[]{1, 2, 3}, ids);
        } finally {
            linker.update("TRUNCATE TABLE test.auto_increment_id");
            linker.update("ALTER TABLE test.auto_increment_id AUTO_INCREMENT = 1");
        }
    }

    private void assertQuery(String sql, ResultSetAssert assertion) {
        try (Connection conn = provider.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    assertion.performAssertion(rs);
                }
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @FunctionalInterface
    private interface ResultSetAssert {
        void performAssertion(ResultSet rs) throws SQLException;
    }

    static class BatchParametersBean {
        private int id;

        @SuppressWarnings("unused")
        public String getLang() {
            return "English";
        }

        @SuppressWarnings("unused")
        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        void setId(int id) {
            this.id = id;
        }
    }
}
