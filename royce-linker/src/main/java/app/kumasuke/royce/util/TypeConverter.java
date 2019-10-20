package app.kumasuke.royce.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypeConverter {
    private static final Map<Class<?>, JDBCType> javaTypeToJdbcTypeMap;
    private static final Map<JDBCType, Class<?>> jdbcTypeToJavaTypeMap;

    static {
        javaTypeToJdbcTypeMap = javaType2jdbcType();
        jdbcTypeToJavaTypeMap = jdbcType2javaType();
    }

    private TypeConverter() {
        throw new UnsupportedOperationException();
    }

    private static Map<Class<?>, JDBCType> javaType2jdbcType() {
        Map<Class<?>, JDBCType> java2jdbc = new HashMap<>();

        java2jdbc.put(String.class, JDBCType.VARCHAR);
        java2jdbc.put(BigDecimal.class, JDBCType.NUMERIC);
        java2jdbc.put(BigInteger.class, JDBCType.BIGINT);

        java2jdbc.put(boolean.class, JDBCType.BIT);
        java2jdbc.put(Boolean.class, JDBCType.BIT);
        java2jdbc.put(byte.class, JDBCType.TINYINT);
        java2jdbc.put(Byte.class, JDBCType.TINYINT);
        java2jdbc.put(short.class, JDBCType.SMALLINT);
        java2jdbc.put(Short.class, JDBCType.SMALLINT);
        java2jdbc.put(int.class, JDBCType.INTEGER);
        java2jdbc.put(Integer.class, JDBCType.INTEGER);
        java2jdbc.put(long.class, JDBCType.BIGINT);
        java2jdbc.put(Long.class, JDBCType.BIGINT);
        java2jdbc.put(float.class, JDBCType.REAL);
        java2jdbc.put(Float.class, JDBCType.REAL);
        java2jdbc.put(double.class, JDBCType.DOUBLE);
        java2jdbc.put(Double.class, JDBCType.DOUBLE);

        java2jdbc.put(byte[].class, JDBCType.VARBINARY);

        java2jdbc.put(java.util.Date.class, JDBCType.DATE);
        java2jdbc.put(java.sql.Date.class, JDBCType.DATE);
        java2jdbc.put(LocalDate.class, JDBCType.DATE);
        java2jdbc.put(java.sql.Time.class, JDBCType.TIME);
        java2jdbc.put(LocalTime.class, JDBCType.TIME);
        java2jdbc.put(java.sql.Timestamp.class, JDBCType.TIMESTAMP);
        java2jdbc.put(LocalDateTime.class, JDBCType.TIMESTAMP);
        java2jdbc.put(ZonedDateTime.class, JDBCType.TIMESTAMP_WITH_TIMEZONE);

        return Collections.unmodifiableMap(java2jdbc);
    }

    private static Map<JDBCType, Class<?>> jdbcType2javaType() {
        Map<JDBCType, Class<?>> jdbc2java = new HashMap<>();

        jdbc2java.put(JDBCType.BIT, Boolean.class);
        jdbc2java.put(JDBCType.TINYINT, Byte.class);
        jdbc2java.put(JDBCType.SMALLINT, Short.class);
        jdbc2java.put(JDBCType.INTEGER, Integer.class);
        jdbc2java.put(JDBCType.BIGINT, Long.class);
        jdbc2java.put(JDBCType.FLOAT, Double.class);
        jdbc2java.put(JDBCType.REAL, Float.class);
        jdbc2java.put(JDBCType.DOUBLE, Double.class);
        jdbc2java.put(JDBCType.BOOLEAN, Boolean.class);

        jdbc2java.put(JDBCType.NUMERIC, BigDecimal.class);
        jdbc2java.put(JDBCType.DECIMAL, BigDecimal.class);

        jdbc2java.put(JDBCType.CHAR, String.class);
        jdbc2java.put(JDBCType.VARCHAR, String.class);
        jdbc2java.put(JDBCType.LONGVARCHAR, String.class);
        jdbc2java.put(JDBCType.NCHAR, String.class);
        jdbc2java.put(JDBCType.NVARCHAR, String.class);
        jdbc2java.put(JDBCType.LONGNVARCHAR, String.class);

        jdbc2java.put(JDBCType.DATE, LocalDate.class);
        jdbc2java.put(JDBCType.TIME, LocalTime.class);
        jdbc2java.put(JDBCType.TIMESTAMP, LocalDateTime.class);
        jdbc2java.put(JDBCType.TIME_WITH_TIMEZONE, ZonedDateTime.class);
        jdbc2java.put(JDBCType.TIMESTAMP_WITH_TIMEZONE, ZonedDateTime.class);

        jdbc2java.put(JDBCType.BINARY, byte[].class);
        jdbc2java.put(JDBCType.VARBINARY, byte[].class);
        jdbc2java.put(JDBCType.LONGVARBINARY, byte[].class);

        jdbc2java.put(JDBCType.NULL, Void.class);
        jdbc2java.put(JDBCType.OTHER, Void.class);
        jdbc2java.put(JDBCType.JAVA_OBJECT, Void.class);
        jdbc2java.put(JDBCType.DISTINCT, Void.class);

        jdbc2java.put(JDBCType.ARRAY, Array.class);
        jdbc2java.put(JDBCType.BLOB, Blob.class);
        jdbc2java.put(JDBCType.CLOB, Clob.class);
        jdbc2java.put(JDBCType.REF, Ref.class);
        jdbc2java.put(JDBCType.DATALINK, URL.class);
        jdbc2java.put(JDBCType.NCLOB, NClob.class);
        jdbc2java.put(JDBCType.SQLXML, SQLXML.class);

        jdbc2java.put(JDBCType.ROWID, RowId.class);
        jdbc2java.put(JDBCType.REF_CURSOR, ResultSet.class);

        return Collections.unmodifiableMap(jdbc2java);
    }

    public static Class<?> jdbcTypeToJavaType(JDBCType jdbcType) {
        if (jdbcTypeToJavaTypeMap.containsKey(jdbcType)) {
            return jdbcTypeToJavaTypeMap.get(jdbcType);
        } else {
            throw new AssertionError("Shouldn't happen");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static JDBCType javaTypeToJdbcType(Class<?> javaType) {
        if (javaTypeToJdbcTypeMap.containsKey(javaType)) {
            return javaTypeToJdbcTypeMap.get(javaType);
        } else {
            throw new IllegalArgumentException("Cannot convert '" + javaType.getCanonicalName() +
                                                       "' to any JDBCType");
        }
    }
}
