package app.kumasuke.test.royce.mapper;

import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LabelMapResultSetMapperTest {
    @Test
    void mapRow() throws SQLException, InvocationTargetException {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("order_id", 1L);
        expected.put("book_id", 1);
        expected.put("user_id", 1);
        expected.put("order_at", LocalDate.now());

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(metaData.getColumnCount()).thenReturn(4);
        when(metaData.getColumnLabel(1)).thenReturn("order_id");
        when(metaData.getColumnType(1)).thenReturn(Types.BIGINT);
        when(metaData.getColumnLabel(2)).thenReturn("book_id");
        when(metaData.getColumnType(2)).thenReturn(Types.INTEGER);
        when(metaData.getColumnLabel(3)).thenReturn("user_id");
        when(metaData.getColumnType(3)).thenReturn(Types.INTEGER);
        when(metaData.getColumnLabel(4)).thenReturn("order_at");
        when(metaData.getColumnType(4)).thenReturn(Types.DATE);

        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getObject(1, Long.class)).thenReturn((Long) expected.get("order_id"));
        when(resultSet.getObject(2, Integer.class)).thenReturn((Integer) expected.get("book_id"));
        when(resultSet.getObject(3, Integer.class)).thenReturn((Integer) expected.get("user_id"));
        when(resultSet.getObject(4, LocalDate.class)).thenReturn((LocalDate) expected.get("order_at"));

        when(resultSet.getMetaData()).thenReturn(metaData);

        Object mapper = getLabelMapResultSetMapperInstance();
        // cast error will lead to test fail
        @SuppressWarnings("unchecked")
        Map<String, Object> actual = (Map<String, Object>) Reflects.invokeVirtual(mapper, "mapRow",
                                                                                  resultSet);
        assertEquals(expected, actual);
    }

    private Object getLabelMapResultSetMapperInstance() throws InvocationTargetException {
        return Reflects.invokeStatic("app.kumasuke.royce.mapper.LabelMapResultSetMapper",
                                     "getInstance");
    }
}
