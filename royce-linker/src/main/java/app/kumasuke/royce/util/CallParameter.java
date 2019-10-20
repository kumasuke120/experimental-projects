package app.kumasuke.royce.util;

import java.sql.JDBCType;
import java.util.Objects;

/**
 * Represents a parameter in the stored procedures, which has three types: {@link ParameterType#IN},
 * {@link ParameterType#OUT}, {@link ParameterType#INOUT}
 *
 * @param <T>
 */
public class CallParameter<T> {
    private final Class<? extends T> valueType;
    private final JDBCType jdbcType;
    private final ParameterType parameterType;
    private T value;

    private CallParameter(T value, Class<? extends T> valueType, ParameterType parameterType) {
        this(value, valueType, toJDBCType(valueType), parameterType);
    }

    private CallParameter(T value, Class<? extends T> valueType, JDBCType jdbcType,
                          ParameterType parameterType) {
        this.value = value;
        this.valueType = valueType;
        this.jdbcType = jdbcType;
        this.parameterType = parameterType;
    }

    @SafeVarargs
    public static <I> CallParameter<I> in(I value, I... ignored) {
        Class<? extends I> valueType = getComponentType(ignored);
        return new CallParameter<>(value, valueType, ParameterType.IN);
    }

    @SafeVarargs
    public static <I> CallParameter<I> in(I value, JDBCType jdbcType, I... ignored) {
        Class<? extends I> valueType = getComponentType(ignored);
        return new CallParameter<>(value, valueType, jdbcType, ParameterType.IN);
    }

    public static <O> CallParameter<O> out(Class<? extends O> valueType) {
        return new CallParameter<>(null, valueType, ParameterType.OUT);
    }

    @SafeVarargs
    public static <O> CallParameter<O> out(O... ignored) {
        Class<? extends O> valueType = getComponentType(ignored);
        return new CallParameter<>(null, valueType, ParameterType.OUT);
    }

    @SafeVarargs
    public static <O> CallParameter<O> out(JDBCType jdbcType, O... ignored) {
        Class<? extends O> valueType = getComponentType(ignored);
        return new CallParameter<>(null, valueType, jdbcType, ParameterType.OUT);
    }

    @SafeVarargs
    public static <IO> CallParameter<IO> inout(IO value, JDBCType jdbcType, IO... ignored) {
        Class<? extends IO> valueType = getComponentType(ignored);
        return new CallParameter<>(value, valueType, jdbcType, ParameterType.INOUT);
    }

    @SafeVarargs
    public static <IO> CallParameter<IO> inout(IO value, IO... ignored) {
        Class<? extends IO> valueType = getComponentType(ignored);
        return new CallParameter<>(value, valueType, ParameterType.INOUT);
    }

    private static <R> Class<? extends R> getComponentType(R[] classes) {
        @SuppressWarnings("unchecked")
        Class<? extends R> componentType = (Class<? extends R>) classes.getClass().getComponentType();
        return componentType;
    }

    private static JDBCType toJDBCType(Class<?> javaType) {
        JDBCType jdbcType = TypeConverter.javaTypeToJdbcType(javaType);
        if (jdbcType != null) {
            return jdbcType;
        } else {
            throw new IllegalArgumentException("there is no default map for type '" +
                                                       javaType.getCanonicalName() + "', please set it explicitly");
        }
    }

    public T getValue() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
    }

    public Class<? extends T> getValueType() {
        return valueType;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallParameter<?> that = (CallParameter<?>) o;
        return Objects.equals(valueType, that.valueType) &&
                jdbcType == that.jdbcType &&
                parameterType == that.parameterType &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueType, jdbcType, parameterType, value);
    }

    @Override
    public String toString() {
        return "<" + value + ": " + parameterType + ">";
    }

    public enum ParameterType {
        IN,
        OUT,
        INOUT
    }
}
