package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.linker.CallableLinker;
import app.kumasuke.royce.linker.Linkers;
import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.CallParameter;
import app.kumasuke.test.util.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CallableLinkerTest {
    private static ConnectionProvider provider;
    private CallableLinker linker;
    private Connection conn;

    @BeforeAll
    static void initAll() {
        provider = TestDatabase.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        conn = provider.getConnection();
        linker = Linkers.callable(conn);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    private void assertAndClearSequence(CallParameter<String> seqName, int initialValue) throws SQLException {
        try (Connection conn = provider.getConnection()) {
            try (PreparedStatement ps =
                         conn.prepareStatement("SELECT `value` FROM test.sequences WHERE name = ?")) {
                ps.setString(1, seqName.getValue());
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(initialValue, rs.getInt(1));
                }
            } finally {
                linker.call("{call test.seq_drop(?)}", seqName);
            }
        }
    }

    @Test
    void call() throws SQLException {
        CallParameter<String> seqName = CallParameter.in("test_call");
        int updateCount = linker.call("{call test.seq_make_2(?, 8)}", seqName);
        assertEquals(1, updateCount);
        assertAndClearSequence(seqName, 8);
    }

    @Test
    void namedCall() throws SQLException {
        CallableLinker mock = mock(CallableLinker.class);
        when(mock.namedCall(anyString())).thenCallRealMethod();
        when(mock.namedCall(anyString(), any())).thenAnswer(mck -> {
            Map<String, CallParameter> parameters = mck.getArgument(1);
            assertNull(parameters);
            return 0;
        });
        mock.namedCall("SELECT 1");

        CallParameter<String> seqName = CallParameter.in("test_namedCall");
        int updateCount = linker.namedCall("{call test.seq_make_2(:sequenceName, 88)}",
                                           Collections.singletonMap("sequenceName", seqName));
        assertEquals(1, updateCount);
        assertAndClearSequence(seqName, 88);
    }

    @Test
    void callAndReturnOne() throws SQLException {
        CallParameter<Integer> id = CallParameter.in(1);
        int retId = linker.callAndReturnOne(rs -> rs.getInt(1), "{call test.get_books_by_id(?)}",
                                            id).orElseThrow(AssertionError::new);
        assertEquals((int) id.getValue(), retId);
    }

    @Test
    void namedCallAndReturnOne() throws SQLException {
        CallableLinker mock = mock(CallableLinker.class);
        when(mock.namedCallAndReturnOne(any(), anyString())).thenCallRealMethod();
        when(mock.namedCallAndReturnOne(any(), anyString(), any())).thenAnswer(mck -> {
            Map<String, CallParameter> parameters = mck.getArgument(2);
            assertNull(parameters);
            ResultSetMapper mapper = mck.getArgument(0);
            return Optional.ofNullable(mapper.mapRow(null));
        });
        int retId0 = mock.namedCallAndReturnOne(rs -> 1, "SELECT 1")
                .orElseThrow(AssertionError::new);
        assertEquals(1, retId0);

        CallParameter<Integer> id = CallParameter.in(1);
        int retId = linker.namedCallAndReturnOne(rs -> rs.getInt(1),
                                                 "{call test.get_books_by_id(:id)}",
                                                 Collections.singletonMap("id", id))
                .orElseThrow(AssertionError::new);
        assertEquals((int) id.getValue(), retId);
    }

    @Test
    void callAndReturnMany() throws SQLException {
        CallParameter<String> language = CallParameter.in("日本語");
        CallParameter<Integer> total = CallParameter.out();
        List<Integer> retIds = linker.callAndReturnMany
                (rs -> rs.getInt(1), "{call test.get_books_by_language(?, ?)}", language, total)
                .collect(Collectors.toList());
        assertEquals(retIds, Arrays.asList(11, 12));
        assertEquals(2, (int) total.getValue());

        CallParameter<Integer> page = CallParameter.in(3);
        CallParameter<Integer> pageSize = CallParameter.inout(5);
        List<Integer> retIds2 = linker.callAndReturnMany
                (rs -> rs.getInt(1), "{call test.get_books_by_page(?, ?)}", page, pageSize)
                .collect(Collectors.toList());
        assertEquals(retIds2, Arrays.asList(11, 12));
        assertEquals(2, (int) pageSize.getValue());
    }

    @Test
    void namedCallAndReturnMany() throws SQLException {
        CallableLinker mock = mock(CallableLinker.class);
        when(mock.namedCallAndReturnMany(any(), anyString())).thenCallRealMethod();
        when(mock.namedCallAndReturnMany(any(), anyString(), any())).thenAnswer(mck -> {
            Map<String, CallParameter> parameters = mck.getArgument(2);
            assertNull(parameters);
            ResultSetMapper mapper = mck.getArgument(0);
            return Stream.of(mapper.mapRow(null));
        });
        List<Integer> retList = mock.namedCallAndReturnMany(rs -> Arrays.asList(1, 2, 3), "SELECT 1")
                .findFirst().orElseGet(Collections::emptyList);
        assertEquals(Arrays.asList(1, 2, 3), retList);

        CallParameter<String> language = CallParameter.in("日本語");
        CallParameter<Integer> total = CallParameter.out();
        Map<String, CallParameter> parameters = new HashMap<>();
        parameters.put("language", language);
        parameters.put("total", total);
        List<Integer> retIds = linker.namedCallAndReturnMany
                (rs -> rs.getInt(1), "{call test.get_books_by_language(:language, :total)}",
                 parameters)
                .collect(Collectors.toList());
        assertEquals(retIds, Arrays.asList(11, 12));
        assertEquals(2, (int) total.getValue());

        CallParameter<Integer> page = CallParameter.in(3);
        CallParameter<Integer> pageSize = CallParameter.inout(5);
        Map<String, CallParameter> parameters2 = new HashMap<>();
        parameters2.put("page", page);
        parameters2.put("pageSize", pageSize);
        List<Integer> retIds2 = linker.namedCallAndReturnMany
                (rs -> rs.getInt(1), "{call test.get_books_by_page(:page, :pageSize)}",
                 parameters2)
                .collect(Collectors.toList());
        assertEquals(retIds2, Arrays.asList(11, 12));
        assertEquals(2, (int) pageSize.getValue());
    }
}
