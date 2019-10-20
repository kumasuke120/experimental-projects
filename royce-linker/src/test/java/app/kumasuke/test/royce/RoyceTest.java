package app.kumasuke.test.royce;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.Royce;
import app.kumasuke.royce.VoidAccessor;
import app.kumasuke.royce.except.UncheckedSQLException;
import app.kumasuke.royce.linker.*;
import app.kumasuke.royce.util.CallParameter;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

class RoyceTest {
    private static ConnectionProvider provider;
    private Royce[] royces;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() {
        royces = new Royce[2];
        royces[0] = Royce.nonTransactional(provider);
        royces[1] = Royce.transactional(provider);
    }

    @Test
    void read() {
        for (Royce royce : royces) {
            royce.read(linker -> {
                int count = linker.selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books")
                        .orElseThrow(AssertionError::new);
                assertEquals(12, count);
            });
            int count = royce.read(linker -> {
                return linker.selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books")
                        .orElseThrow(AssertionError::new);
            });
            assertEquals(12, count);
        }
    }

    @Test
    void tryRead() {
        final SQLException theException = new SQLException();
        for (Royce royce : royces) {
            final boolean[] executedRef = {false};
            assertFalse(royce.tryRead(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                return true;
            }).handle(e -> {
                assertSame(theException, e);
                return false;
            }));
            assertTrue(executedRef[0]);

            executedRef[0] = false;
            royce.tryRead(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                executedRef[0] = false;
            }).handle(e -> assertSame(theException, e));
            assertTrue(executedRef[0]);
        }
    }

    @Test
    void readShouldBeReadOnly() {
        for (Royce royce : royces) {
            assertThrows(UncheckedSQLException.class, () -> royce.read(linker -> {
                linker.<Void>selectOne(rs -> null, "UPDATE test.books SET price = 7.35 WHERE id = 1");
            }));
            assertThrows(UncheckedSQLException.class, () -> royce.read(linker -> {
                linker.<Void>selectOne(rs -> null, "DELETE FROM test.books WHERE id = 1");
            }));
        }
    }

    @Test
    void write() {
        for (Royce royce : royces) {
            royce.write(linker -> {
                int updateCount = linker.update("UPDATE test.books SET price = 7.35 WHERE id = 1");
                assertEquals(1, updateCount);
            });
            int updateCount = royce.write(linker -> {
                return linker.update("UPDATE test.books SET price = 7.34 WHERE id = 1");
            });
            assertEquals(1, updateCount);
        }
    }

    @Test
    void tryWrite() {
        final SQLException theException = new SQLException();
        for (Royce royce : royces) {
            final boolean[] executedRef = {false};
            assertFalse(royce.tryWrite(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                return true;
            }).handle(e -> {
                assertSame(theException, e);
                return false;
            }));
            assertTrue(executedRef[0]);

            executedRef[0] = false;
            royce.tryWrite(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                executedRef[0] = false;
            }).handle(e -> assertSame(theException, e));
            assertTrue(executedRef[0]);
        }
    }

    @Test
    void readWrite() {
        for (Royce royce : royces) {
            royce.readWrite(linker -> {
                double price = linker.selectOne(rs -> rs.getDouble(1),
                                                "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
                assertEquals(7.34, price);

                linker.update("UPDATE test.books SET price = 7.35 WHERE id = 1");
                price = linker.selectOne(rs -> rs.getDouble(1),
                                         "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
                assertEquals(7.35, price);

                linker.update("UPDATE test.books SET price = 7.34 WHERE id = 1");
                price = linker.selectOne(rs -> rs.getDouble(1),
                                         "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
                assertEquals(7.34, price);
            });

            double retPrice = royce.readWrite(linker -> {
                final double price = linker.selectOne(rs -> rs.getDouble(1),
                                                      "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
                assertEquals(7.34, price);

                linker.update("UPDATE test.books SET price = 7.35 WHERE id = 1");
                return linker.selectOne(rs -> rs.getDouble(1),
                                        "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
            });
            assertEquals(7.35, retPrice);

            retPrice = royce.readWrite(linker -> {
                linker.update("UPDATE test.books SET price = 7.34 WHERE id = 1");
                return linker.selectOne(rs -> rs.getDouble(1),
                                        "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
            });
            assertEquals(7.34, retPrice);
        }
    }

    @Test
    void tryReadWrite() {
        final SQLException theException = new SQLException();
        for (Royce royce : royces) {
            final boolean[] executedRef = {false};
            assertFalse(royce.tryReadWrite(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                return true;
            }).handle(e -> {
                assertSame(theException, e);
                return false;
            }));
            assertTrue(executedRef[0]);

            executedRef[0] = false;
            royce.tryReadWrite(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                executedRef[0] = false;
            }).handle(e -> assertSame(theException, e));
            assertTrue(executedRef[0]);
        }
    }

    @Test
    void call() {
        for (Royce royce : royces) {
            royce.call(linker -> {
                CallParameter<String> language = CallParameter.in("English");
                CallParameter<Integer> total = CallParameter.out();
                linker.call("{call test.get_books_by_language(?, ?)}", language, total);
                assertEquals(7, (int) total.getValue());
            });

            int retTotal = royce.call(linker -> {
                CallParameter<String> language = CallParameter.in("English");
                CallParameter<Integer> total = CallParameter.out();
                linker.call("{call test.get_books_by_language(?, ?)}", language, total);
                return total.getValue();
            });
            assertEquals(7, retTotal);
        }
    }

    @Test
    void tryCall() {
        final SQLException theException = new SQLException();
        for (Royce royce : royces) {
            final boolean[] executedRef = {false};
            assertFalse(royce.tryCall(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                return true;
            }).handle(e -> {
                assertSame(theException, e);
                return false;
            }));
            assertTrue(executedRef[0]);

            executedRef[0] = false;
            royce.tryCall(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                executedRef[0] = false;
            }).handle(e -> assertSame(theException, e));
            assertTrue(executedRef[0]);
        }
    }

    @Test
    void link() {
        for (Royce royce : royces) {
            royce.link(linker -> {
                int count = linker.selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books")
                        .orElseThrow(AssertionError::new);
                assertEquals(12, count);

                CallParameter<String> language = CallParameter.in("English");
                CallParameter<Integer> total = CallParameter.out();
                linker.call("{call test.get_books_by_language(?, ?)}", language, total);
                assertEquals(7, (int) total.getValue());
            });

            int result = royce.link(linker -> {
                int count = linker.selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books")
                        .orElseThrow(AssertionError::new);
                CallParameter<String> language = CallParameter.in("English");
                CallParameter<Integer> total = CallParameter.out();
                linker.call("{call test.get_books_by_language(?, ?)}", language, total);
                return count + total.getValue();
            });
            assertEquals(19, result);
        }
    }

    @Test
    void tryLink() {
        final SQLException theException = new SQLException();
        for (Royce royce : royces) {
            final boolean[] executedRef = {false};
            assertFalse(royce.tryLink(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                return true;
            }).handle(e -> {
                assertSame(theException, e);
                return false;
            }));
            assertTrue(executedRef[0]);

            executedRef[0] = false;
            royce.tryLink(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                executedRef[0] = false;
            }).handle(e -> assertSame(theException, e));
            assertTrue(executedRef[0]);
        }
    }

    @Test
    void nativeJdbc() {
        for (Royce royce : royces) {
            royce.nativeJdbc(linker -> {
                Statement stmt = linker.statement();
                assertNotNull(stmt);
            });
            Statement stmt = royce.nativeJdbc(NativeJdbcLinker::statement);
            assertNotNull(stmt);
        }
    }

    @Test
    void tryNativeJdbc() {
        final SQLException theException = new SQLException();
        for (Royce royce : royces) {
            final boolean[] executedRef = {false};
            assertFalse(royce.tryNativeJdbc(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                return true;
            }).handle(e -> {
                assertSame(theException, e);
                return false;
            }));
            assertTrue(executedRef[0]);

            executedRef[0] = false;
            royce.tryNativeJdbc(linker -> {
                executedRef[0] = true;
                throwSQLException(theException);
                executedRef[0] = false;
            }).handle(e -> assertSame(theException, e));
            assertTrue(executedRef[0]);
        }
    }

    @Test
    void linkerCannotBeUsedOutsideClosure() {
        for (Royce royce : royces) {
            assertThrows(IllegalStateException.class, () -> {
                final ReadableLinker[] outLinker = new ReadableLinker[1];
                royce.read(linker -> {
                    outLinker[0] = linker;
                });
                outLinker[0].selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books");
            });
            assertThrows(IllegalStateException.class, () -> {
                final WritableLinker[] outLinker = new WritableLinker[1];
                royce.write((VoidAccessor<WritableLinker>) linker -> outLinker[0] = linker);
                outLinker[0].update("UPDATE test.books SET price = 7.35 WHERE id = 1");
            });
            assertThrows(IllegalStateException.class, () -> {
                final ReadWritableLinker[] outLinker = new ReadWritableLinker[1];
                royce.readWrite(linker -> {
                    outLinker[0] = linker;
                });
                outLinker[0].selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books");
            });
            assertThrows(IllegalStateException.class, () -> {
                final CallableLinker[] outLinker = new CallableLinker[1];
                royce.call(linker -> {
                    outLinker[0] = linker;
                });
                CallParameter<String> language = CallParameter.in("English");
                CallParameter<Integer> total = CallParameter.out();
                outLinker[0].call("{call test.get_books_by_language(?, ?)}", language, total);
            });
            assertThrows(IllegalStateException.class, () -> {
                final UniversalLinker[] outLinker = new UniversalLinker[1];
                royce.link(linker -> {
                    outLinker[0] = linker;
                });
                outLinker[0].selectOne(rs -> rs.getInt(1), "SELECT count(id) FROM test.books");
            });
            assertThrows(IllegalStateException.class, () -> {
                final NativeJdbcLinker[] outLinker = new NativeJdbcLinker[1];
                royce.nativeJdbc(linker -> {
                    outLinker[0] = linker;
                });
                outLinker[0].statement();
            });
        }
    }

    private void throwSQLException(final SQLException e) throws SQLException {
        throw e;
    }

    @Nested
    class NonTransactional {
        private Royce royce;

        @BeforeEach
        void init() {
            royce = Royce.nonTransactional(provider);
        }

        @Test
        void transactional() {
            Royce transactional = royce.transactional();
            assertTrue(royce.isNonTransactional());
            assertFalse(royce.isTransactional());
            assertFalse(transactional.isNonTransactional());
            assertTrue(transactional.isTransactional());
        }

        @Test
        void nonTransactional() {
            Royce nonTransactional = royce.nonTransactional();
            assertTrue(royce.isNonTransactional());
            assertFalse(royce.isTransactional());
            assertTrue(nonTransactional.isNonTransactional());
            assertFalse(nonTransactional.isTransactional());
            assertSame(royce, nonTransactional);
        }

        @Test
        void transactionalAndNonTransactionalShouldBeSingleton() {
            assertTrue(royce.isNonTransactional());
            assertSame(royce, royce.transactional().nonTransactional());
            assertSame(royce.transactional(), royce.transactional());
        }

        @Test
        void autoCommitShouldBeSetTrueWhenNecessary() throws SQLException {
            final Connection theConnection = mock(Connection.class);

            when(theConnection.getAutoCommit()).thenReturn(false);
            final int[] counter = {0};
            doAnswer(mck -> {
                boolean autoCommit = mck.getArgument(0);
                assertEquals(counter[0]++ % 2 == 0, autoCommit);
                return null;
            }).when(theConnection).setAutoCommit(anyBoolean());

            final Royce theRoyce = Royce.nonTransactional(() -> theConnection);

            theRoyce.read(linker -> {
            });
            theRoyce.tryRead(linker -> {
            });
            theRoyce.write(linker -> {
            });
            theRoyce.tryWrite(linker -> {
            });
            theRoyce.readWrite(linker -> {
            });
            theRoyce.tryReadWrite(linker -> {
            });
            theRoyce.link(linker -> {
            });
            theRoyce.tryLink(linker -> {
            });
            theRoyce.nativeJdbc(linker -> {
            });
            theRoyce.tryNativeJdbc(linker -> {
            });

            assertNotEquals(0, counter[0]);
        }
    }

    @Nested
    class TransactionalRoyce {
        private Royce royce;

        @BeforeEach
        void init() {
            royce = Royce.transactional(provider);
        }

        @Test
        void readAndWriteShouldSupportRollbackWhenException() {
            royce.read(linker -> {
                double price = linker.selectOne(rs -> rs.getDouble(1),
                                                "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
                assertEquals(7.34, price);
            });

            assertThrows(RuntimeException.class, () -> royce.write((VoidAccessor<WritableLinker>) linker -> {
                linker.update("UPDATE test.books SET price = 7.35 WHERE id = 1");
                throw new RuntimeException(RoyceTest.this.toString());
            }), RoyceTest.this.toString());

            double price = royce.read(linker -> {
                return linker.selectOne(rs -> rs.getDouble(1),
                                        "SELECT price FROM test.books WHERE id = 1")
                        .orElseThrow(AssertionError::new);
            });
            assertEquals(7.34, price);
        }

        @Test
        void transactional() {
            Royce transactional = royce.transactional();
            assertFalse(royce.isNonTransactional());
            assertTrue(royce.isTransactional());
            assertFalse(transactional.isNonTransactional());
            assertTrue(transactional.isTransactional());
            assertSame(royce, transactional);
        }

        @Test
        void nonTransactional() {
            Royce nonTransactional = royce.nonTransactional();
            assertFalse(royce.isNonTransactional());
            assertTrue(royce.isTransactional());
            assertTrue(nonTransactional.isNonTransactional());
            assertFalse(nonTransactional.isTransactional());
        }

        @Test
        void transactionalAndNonTransactionalShouldBeSingleton() {
            assertTrue(royce.isTransactional());
            assertSame(royce, royce.nonTransactional().transactional());
            assertSame(royce.nonTransactional(), royce.nonTransactional());
        }
    }
}
