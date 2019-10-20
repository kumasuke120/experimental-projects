package app.kumasuke.test.royce.util;

import app.kumasuke.royce.util.CallParameter;
import app.kumasuke.royce.util.CallParameter.ParameterType;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.JDBCType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CallParameterTest {
    @Test
    void in() {
        CallParameter<Integer> in = CallParameter.in(1);
        assertEquals(Integer.class, in.getValueType());
        assertEquals(ParameterType.IN, in.getParameterType());
        assertEquals(JDBCType.INTEGER, in.getJdbcType());

        CallParameter<Integer> in2 = CallParameter.in(1, JDBCType.SMALLINT);
        assertEquals(Integer.class, in2.getValueType());
        assertEquals(ParameterType.IN, in2.getParameterType());
        assertEquals(JDBCType.SMALLINT, in2.getJdbcType());
    }

    @Test
    void out() {
        CallParameter<Integer> out = CallParameter.out();
        assertEquals(Integer.class, out.getValueType());
        assertEquals(ParameterType.OUT, out.getParameterType());
        assertEquals(JDBCType.INTEGER, out.getJdbcType());

        CallParameter<Long> out2 = CallParameter.out(Long.class);
        assertEquals(Long.class, out2.getValueType());
        assertEquals(ParameterType.OUT, out2.getParameterType());
        assertEquals(JDBCType.BIGINT, out2.getJdbcType());

        CallParameter<BigInteger> out3 = CallParameter.out(JDBCType.BIGINT);
        assertEquals(BigInteger.class, out3.getValueType());
        assertEquals(ParameterType.OUT, out3.getParameterType());
        assertEquals(JDBCType.BIGINT, out3.getJdbcType());
    }

    @Test
    void inout() {
        CallParameter<Integer> inout = CallParameter.inout(1);
        assertEquals(Integer.class, inout.getValueType());
        assertEquals(ParameterType.INOUT, inout.getParameterType());
        assertEquals(JDBCType.INTEGER, inout.getJdbcType());

        CallParameter<Integer> in2 = CallParameter.inout(1, JDBCType.SMALLINT);
        assertEquals(Integer.class, in2.getValueType());
        assertEquals(ParameterType.INOUT, in2.getParameterType());
        assertEquals(JDBCType.SMALLINT, in2.getJdbcType());
    }
}
