package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.linker.Linkers;
import app.kumasuke.royce.linker.ReadableLinker;
import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.test.util.Book;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadableLinkerTest {
    private static ConnectionProvider provider;
    private ReadableLinker linker;
    private Connection conn;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        conn = provider.getConnection();
        linker = Linkers.readable(conn);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    void selectOne() throws SQLException {
        Book book = linker.selectOne(Book.mapper(), "SELECT * FROM test.books WHERE id = ?", 1)
                .orElseThrow(AssertionError::new);
        assertEquals(1, (int) book.getId());
        assertEquals("978-0345803481", book.getIsbn());
        assertEquals(LocalDate.of(2012, 4, 3), book.getReleaseDate());

        Book book2 = linker.selectOne(Book.mapper(),
                                      "SELECT * FROM test.books " +
                                              "WHERE author LIKE ? AND `language` = ?",
                                      "J. K. Rowling%", "English")
                .orElseThrow(AssertionError::new);
        assertEquals(6, (int) book2.getId());
        assertEquals("978-0545139700", book2.getIsbn());
        assertEquals(LocalDate.of(2009, 7, 7), book2.getReleaseDate());
    }

    @Test
    void namedSelectOne() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadableLinker mock = mock(ReadableLinker.class);
        when(mock.namedSelectOne(any(), anyString())).thenCallRealMethod();
        when(mock.namedSelectOne(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            ResultSetMapper mapper = mck.getArgument(0);
            Map<String, Object> parameters = mck.getArgument(2);
            assertNull(parameters);
            return Optional.ofNullable(mapper.mapRow(null));
        });
        final Book dummy = new Book();
        Book retBook = mock.namedSelectOne(rs -> dummy, "SELECT 1").orElseThrow(AssertionError::new);
        assertTrue(invokedRef[0]);
        assertEquals(dummy, retBook);

        Book book = linker.namedSelectOne(Book.mapper(),
                                          "SELECT * FROM test.books WHERE id >= :id AND id <= :id",
                                          Collections.singletonMap("id", 1)).orElseThrow(AssertionError::new);
        assertEquals(1, (int) book.getId());
        assertEquals("978-0345803481", book.getIsbn());
        assertEquals(LocalDate.of(2012, 4, 3), book.getReleaseDate());

        Book book2 = linker.namedSelectOne(Book.mapper(),
                                           "SELECT * FROM test.books WHERE id = :id",
                                           book).orElseThrow(AssertionError::new);
        assertEquals(book, book2);
    }

    @Test
    void selectMany() throws SQLException {
        List<Book> books = linker.selectMany(Book.mapper(),
                                             "SELECT * FROM test.books WHERE price > ? AND language = ? ",
                                             15, "English").collect(Collectors.toList());
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getId() == 10));
        assertTrue(books.stream().anyMatch(b -> b.getId() == 3));
    }

    @Test
    void namedSelectMany() throws SQLException {
        final boolean[] invokedRef = {false};
        ReadableLinker mock = mock(ReadableLinker.class);
        when(mock.namedSelectMany(any(), anyString())).thenCallRealMethod();
        when(mock.namedSelectMany(any(), anyString(), any())).thenAnswer(mck -> {
            invokedRef[0] = true;
            ResultSetMapper mapper = mck.getArgument(0);
            Map<String, Object> parameters = mck.getArgument(2);
            assertNull(parameters);
            return Stream.of(mapper.mapRow(null));
        });
        final Book dummy = new Book();
        List<Book> retBooks = mock.namedSelectMany(rs -> dummy, "SELECT 1").collect(Collectors.toList());
        assertTrue(invokedRef[0]);
        assertEquals(Collections.singletonList(dummy), retBooks);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("priceMin", 15);
        parameters.put("language", "English");
        List<Book> books = linker.namedSelectMany(Book.mapper(),
                                                  "SELECT * FROM test.books " +
                                                          "WHERE price > :priceMin AND language = :language ",
                                                  parameters).collect(Collectors.toList());
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getId() == 10));
        assertTrue(books.stream().anyMatch(b -> b.getId() == 3));

        ParameterBean bean = new ParameterBean();
        List<Book> books2 = linker.namedSelectMany(Book.mapper(),
                                                   "SELECT * FROM test.books " +
                                                           "WHERE price > :priceMin AND language = :language ",
                                                   bean).collect(Collectors.toList());
        assertEquals(books, books2);
    }

    @SuppressWarnings({"WeakerAccess", "SameReturnValue"})
    public static class ParameterBean {
        @SuppressWarnings({"unused", "SameReturnValue"})
        public double getPriceMin() {
            return 15;
        }

        @SuppressWarnings("unused")
        public String getLanguage() {
            return "English";
        }
    }
}
