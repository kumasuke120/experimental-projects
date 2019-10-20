package app.kumasuke.test.royce.util;

import app.kumasuke.royce.util.ResultSets;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultSetsTest {
    @Test
    void getValue() throws SQLException {
        final ResultSet resultSet = mock(ResultSet.class);

        /*
         * 1:    null
         * 2:    (byte) 1
         * 3:    (short) 2
         * 4:    (int) 4
         * 5:    (String) "String"
         * */
        when(resultSet.getShort(anyInt())).thenAnswer(mck -> {
            int index = mck.getArgument(0);
            if (index == 2) {
                return (short) 1;
            } else if (index == 3) {
                return (short) 2;
            } else if (index == 4) {
                return (short) 4;
            } else {
                throw new SQLException();
            }
        });
        when(resultSet.getByte(anyInt())).thenAnswer(mck -> {
            int index = mck.getArgument(0);
            if (index == 2) {
                return (byte) 1;
            } else if (index == 3) {
                return (byte) 2;
            } else if (index == 4) {
                return (byte) 4;
            } else {
                throw new SQLException();
            }
        });
        //noinspection unchecked
        when(resultSet.getObject(anyInt(), any(Class.class))).thenAnswer(mck -> {
            int index = mck.getArgument(0);
            Class<?> javaType = mck.getArgument(1);

            if (index == 1) {
                return null;
            } else if (index == 2) {
                if (Byte.class.equals(javaType) || byte.class.equals(javaType)) {
                    return (byte) 1;
                } else {
                    throw new SQLException();
                }
            } else if (index == 3) {
                if (Short.class.equals(javaType) || short.class.equals(javaType)) {
                    return (short) 2;
                } else {
                    throw new SQLException();
                }
            } else if (index == 4) {
                if (Integer.class.equals(javaType) || int.class.equals(javaType)) {
                    return 4;
                } else {
                    throw new SQLException();
                }
            } else if (index == 5) {
                if (String.class.equals(javaType)) {
                    return "String";
                } else {
                    throw new SQLException();
                }
            } else {
                throw new SQLException();
            }
        });

        when(resultSet.getObject(anyInt())).thenAnswer(mck -> {
            int index = mck.getArgument(0);
            if (index == 1) {
                return null;
            } else if (index == 2) {
                return (byte) 1;
            } else if (index == 3) {
                return (short) 2;
            } else if (index == 4) {
                return 4;
            } else if (index == 5) {
                return "String";
            } else {
                throw new SQLException();
            }
        });

        assertNull(ResultSets.getValue(resultSet, 1, Byte.class));
        assertNull(ResultSets.getValue(resultSet, 1, Short.class));

        assertEquals((byte) 1, ResultSets.getValue(resultSet, 2, Byte.class));
        assertEquals((byte) 1, ResultSets.getValue(resultSet, 2, byte.class));
        assertEquals((short) 1, ResultSets.getValue(resultSet, 2, Short.class));
        assertEquals((short) 1, ResultSets.getValue(resultSet, 2, short.class));


        assertEquals((byte) 2, ResultSets.getValue(resultSet, 3, Byte.class));
        assertEquals((byte) 2, ResultSets.getValue(resultSet, 3, byte.class));
        assertEquals((short) 2, ResultSets.getValue(resultSet, 3, Short.class));
        assertEquals((short) 2, ResultSets.getValue(resultSet, 3, short.class));

        assertEquals((byte) 4, ResultSets.getValue(resultSet, 4, Byte.class));
        assertEquals((byte) 4, ResultSets.getValue(resultSet, 4, byte.class));
        assertEquals((short) 4, ResultSets.getValue(resultSet, 4, Short.class));
        assertEquals((short) 4, ResultSets.getValue(resultSet, 4, short.class));
        assertEquals(4, ResultSets.getValue(resultSet, 4, Integer.class));
        assertEquals(4, ResultSets.getValue(resultSet, 4, int.class));

        assertEquals("String", ResultSets.getValue(resultSet, 5, String.class));
        assertThrows(SQLException.class, () -> ResultSets.getValue(resultSet, 5, Byte.class));
        assertThrows(SQLException.class, () -> ResultSets.getValue(resultSet, 5, byte.class));
        assertThrows(SQLException.class, () -> ResultSets.getValue(resultSet, 5, Short.class));
        assertThrows(SQLException.class, () -> ResultSets.getValue(resultSet, 5, short.class));
        assertThrows(SQLException.class, () -> ResultSets.getValue(resultSet, 5, Integer.class));
        assertThrows(SQLException.class, () -> ResultSets.getValue(resultSet, 5, int.class));
    }
}
