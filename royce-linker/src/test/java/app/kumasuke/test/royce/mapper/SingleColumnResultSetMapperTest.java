package app.kumasuke.test.royce.mapper;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SingleColumnResultSetMapperTest {
    @Test
    void getInstance() throws SQLException, InvocationTargetException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getObject(1, Integer.class)).thenReturn(8);
        ResultSetMapper<Integer> mapper = getInstanceOfInteger();
        int actual = mapper.mapRow(resultSet);
        assertEquals(8, actual);
    }

    @Test
    void getInstanceShouldBeSingleton() throws InvocationTargetException {
        ResultSetMapper<Integer> first = getInstanceOfInteger();
        ResultSetMapper<Integer> second = getInstanceOfInteger();
        assertSame(first, second);
    }

    // cast error will lead to test fail
    @SuppressWarnings("unchecked")
    private ResultSetMapper<Integer> getInstanceOfInteger() throws InvocationTargetException {
        return (ResultSetMapper<Integer>)
                Reflects.invokeStatic("app.kumasuke.royce.mapper.SingleColumnResultSetMapper",
                                      "getInstance",
                                      Integer.class);
    }
}
