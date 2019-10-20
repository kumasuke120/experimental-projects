package app.kumasuke.test.royce.mapper;

import app.kumasuke.royce.mapper.Mappers;
import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.test.util.Book;
import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MappersTest {
    private static Random rand;

    @BeforeAll
    static void initAll() {
        rand = new SecureRandom();
    }

    private static IntStream sixRandomIndices() {
        return IntStream.range(0, 6)
                .map(i -> rand.nextInt(10) + 1);
    }

    @Test
    void mappersCannotBeInstantiated() {
        // new Mappers();

        InvocationTargetException e = assertThrows(InvocationTargetException.class,
                                                   () -> Reflects.newInstance(Mappers.class));
        assertTrue(e.getCause() instanceof UnsupportedOperationException);
    }

    @Test
    void firstColumnToBoolean() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Boolean.class)).thenReturn(true);

        ResultSetMapper<Boolean> mapper = Mappers.firstColumnToBoolean();
        Boolean result = mapper.mapRow(mock);
        assertTrue(result);
    }

    @Test
    void firstColumnToByte() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Byte.class)).thenReturn((byte) 1);
        when(mock.getObject(1)).thenReturn(1);

        ResultSetMapper<Byte> mapper = Mappers.firstColumnToByte();
        byte result = mapper.mapRow(mock);
        assertEquals((byte) 1, result);
    }

    @Test
    void firstColumnToShort() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Short.class)).thenReturn((short) 1);
        when(mock.getObject(1)).thenReturn(1);

        ResultSetMapper<Short> mapper = Mappers.firstColumnToShort();
        short result = mapper.mapRow(mock);
        assertEquals((short) 1, result);
    }

    @Test
    void firstColumnToInteger() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Integer.class)).thenReturn(1);

        ResultSetMapper<Integer> mapper = Mappers.firstColumnToInteger();
        int result = mapper.mapRow(mock);
        assertEquals(1, result);
    }

    @Test
    void firstColumnToLong() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Long.class)).thenReturn(1L);

        ResultSetMapper<Long> mapper = Mappers.firstColumnToLong();
        long result = mapper.mapRow(mock);
        assertEquals(1L, result);
    }

    @Test
    void firstColumnToFloat() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Float.class)).thenReturn(1F);

        ResultSetMapper<Float> mapper = Mappers.firstColumnToFloat();
        float result = mapper.mapRow(mock);
        assertEquals(1F, result);
    }

    @Test
    void firstColumnToDouble() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, Double.class)).thenReturn(1.0);

        ResultSetMapper<Double> mapper = Mappers.firstColumnToDouble();
        double result = mapper.mapRow(mock);
        assertEquals(1.0, result);
    }

    @Test
    void firstColumnToBigInteger() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, BigInteger.class)).thenReturn(BigInteger.ONE);

        ResultSetMapper<BigInteger> mapper = Mappers.firstColumnToBigInteger();
        BigInteger result = mapper.mapRow(mock);
        assertEquals(BigInteger.ONE, result);
    }

    @Test
    void firstColumnToBigDecimal() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, BigDecimal.class)).thenReturn(BigDecimal.ONE);

        ResultSetMapper<BigDecimal> mapper = Mappers.firstColumnToBigDecimal();
        BigDecimal result = mapper.mapRow(mock);
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    void firstColumnToString() throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1, String.class)).thenReturn("test");

        ResultSetMapper<String> mapper = Mappers.firstColumnToString();
        String result = mapper.mapRow(mock);
        assertEquals("test", result);
    }

    @ParameterizedTest
    @ValueSource(classes = {Boolean.class, Byte.class, Integer.class, Long.class, Double.class, String.class})
    void firstColumnTo(Class<?> candidate) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        boolean[] invokedRef = {false};
        Object[] retValRef = new Object[1];
        when(mock.getObject(1, candidate)).thenAnswer(mck -> {
            Class<?> clz = mck.getArgument(1);
            Object retVal = Reflects.getTypeDefaultValue(clz);
            retValRef[0] = retVal;
            invokedRef[0] = true;
            return retVal;
        });
        when(mock.getObject(1)).thenAnswer(mck -> {
            Object retVal = Reflects.getTypeDefaultValue(candidate);
            retValRef[0] = retVal;
            invokedRef[0] = true;
            return retVal;
        });

        ResultSetMapper<?> mapper = Mappers.firstColumnTo(candidate);
        Object result = mapper.mapRow(mock);
        assertTrue(invokedRef[0]);
        assertSame(retValRef[0], result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToBoolean(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Boolean.class)).thenReturn(true);

        ResultSetMapper<Boolean> mapper = Mappers.columnToBoolean(index);
        Boolean result = mapper.mapRow(mock);
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToByte(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Byte.class)).thenReturn((byte) 1);
        when(mock.getObject(index)).thenReturn(1);

        ResultSetMapper<Byte> mapper = Mappers.columnToByte(index);
        byte result = mapper.mapRow(mock);
        assertEquals((byte) 1, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToShort(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Short.class)).thenReturn((short) 1);
        when(mock.getObject(index)).thenReturn(1);

        ResultSetMapper<Short> mapper = Mappers.columnToShort(index);
        short result = mapper.mapRow(mock);
        assertEquals((short) 1, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToInteger(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Integer.class)).thenReturn(1);

        ResultSetMapper<Integer> mapper = Mappers.columnToInteger(index);
        int result = mapper.mapRow(mock);
        assertEquals(1, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToLong(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Long.class)).thenReturn(1L);

        ResultSetMapper<Long> mapper = Mappers.columnToLong(index);
        long result = mapper.mapRow(mock);
        assertEquals(1L, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToFloat(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Float.class)).thenReturn(1F);

        ResultSetMapper<Float> mapper = Mappers.columnToFloat(index);
        float result = mapper.mapRow(mock);
        assertEquals(1F, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToDouble(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, Double.class)).thenReturn(1.0);

        ResultSetMapper<Double> mapper = Mappers.columnToDouble(index);
        double result = mapper.mapRow(mock);
        assertEquals(1.0, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToBigInteger(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, BigInteger.class)).thenReturn(BigInteger.ONE);

        ResultSetMapper<BigInteger> mapper = Mappers.columnToBigInteger(index);
        BigInteger result = mapper.mapRow(mock);
        assertEquals(BigInteger.ONE, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void firstColumnToBigDecimal(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, BigDecimal.class)).thenReturn(BigDecimal.ONE);

        ResultSetMapper<BigDecimal> mapper = Mappers.columnToBigDecimal(index);
        BigDecimal result = mapper.mapRow(mock);
        assertEquals(BigDecimal.ONE, result);
    }

    @ParameterizedTest
    @MethodSource("sixRandomIndices")
    void columnToString(int index) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(index, String.class)).thenReturn("test");

        ResultSetMapper<String> mapper = Mappers.columnToString(index);
        String result = mapper.mapRow(mock);
        assertEquals("test", result);
    }

    @ParameterizedTest
    @ValueSource(classes = {Boolean.class, Byte.class, Integer.class, Long.class, Double.class, String.class})
    void columnTo(Class<?> candidate) throws SQLException {
        for (int index : sixRandomIndices().toArray()) {
            ResultSet mock = mock(ResultSet.class);
            boolean[] invokedRef = {false};
            Object[] retValRef = new Object[1];
            when(mock.getObject(index, candidate)).thenAnswer(mck -> {
                Class<?> clz = mck.getArgument(1);
                Object retVal = Reflects.getTypeDefaultValue(clz);
                retValRef[0] = retVal;
                invokedRef[0] = true;
                return retVal;
            });
            when(mock.getObject(index)).thenAnswer(mck -> {
                Object retVal = Reflects.getTypeDefaultValue(candidate);
                retValRef[0] = retVal;
                invokedRef[0] = true;
                return retVal;
            });

            ResultSetMapper<?> mapper = Mappers.columnTo(candidate, index);
            Object result = mapper.mapRow(mock);
            assertTrue(invokedRef[0]);
            assertSame(retValRef[0], result);
        }
    }

    @Test
    void toBean() {
        ResultSetMapper<Book> mapper = Mappers.toBean(Book.class);
        assertNotNull(mapper);
    }

    @Test
    void toMap() {
        ResultSetMapper<Map<String, Object>> mapper = Mappers.toMap();
        assertNotNull(mapper);
    }
}
