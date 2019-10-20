package app.kumasuke.royce.mapper;

import javax.annotation.Nonnull;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSetMetaData;
import java.util.Map;

/**
 * An utility class that contains lots of useful methods to create or get
 * <code>ResultSetMapper</code> easily and quickly
 */
public class Mappers {
    private Mappers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Boolean</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Boolean> firstColumnToBoolean() {
        return SingleColumnResultSetMapper.getInstance(Boolean.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Byte</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Byte> firstColumnToByte() {
        return SingleColumnResultSetMapper.getInstance(Byte.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Short</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Short> firstColumnToShort() {
        return SingleColumnResultSetMapper.getInstance(Short.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Integer</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Integer> firstColumnToInteger() {
        return SingleColumnResultSetMapper.getInstance(Integer.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Long</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Long> firstColumnToLong() {
        return SingleColumnResultSetMapper.getInstance(Long.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Long</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Float> firstColumnToFloat() {
        return SingleColumnResultSetMapper.getInstance(Float.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>Double</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Double> firstColumnToDouble() {
        return SingleColumnResultSetMapper.getInstance(Double.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>BigInteger</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<BigInteger> firstColumnToBigInteger() {
        return SingleColumnResultSetMapper.getInstance(BigInteger.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>BigDecimal</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<BigDecimal> firstColumnToBigDecimal() {
        return SingleColumnResultSetMapper.getInstance(BigDecimal.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to <code>String</code>.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<String> firstColumnToString() {
        return SingleColumnResultSetMapper.getInstance(String.class);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the first column of the <code>ResultSet</code>
     * to the given type <code>&lt;T&gt;</code>.
     *
     * @param clazz the <code>Class</code> of the type needed
     * @param <T>   the needed type
     * @return a <code>ResultSetMapper</code>
     */
    public static <T> ResultSetMapper<T> firstColumnTo(@Nonnull Class<T> clazz) {
        return SingleColumnResultSetMapper.getInstance(clazz);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Boolean</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Boolean> columnToBoolean(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Boolean.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Byte</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Byte> columnToByte(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Byte.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Short</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Short> columnToShort(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Short.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Integer</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Integer> columnToInteger(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Integer.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Long</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Long> columnToLong(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Long.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Float</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Float> columnToFloat(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Float.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>Double</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Double> columnToDouble(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(Double.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>BigInteger</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<BigInteger> columnToBigInteger(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(BigInteger.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>BigDecimal</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<BigDecimal> columnToBigDecimal(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(BigDecimal.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * <code>String</code>.
     *
     * @param columnIndex the index of column to be converted
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<String> columnToString(int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(String.class, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts the column with given index to
     * the given type <code>&lt;T&gt;</code>.
     *
     * @param clazz       the <code>Class</code> of the type needed
     * @param columnIndex the index of column to be converted
     * @param <T>         the needed type
     * @return a <code>ResultSetMapper</code>
     */
    public static <T> ResultSetMapper<T> columnTo(@Nonnull Class<T> clazz, int columnIndex) {
        return SingleColumnResultSetMapper.getInstance(clazz, columnIndex);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts columns to Bean of required type
     * <code>&lt;T&gt;</code> based on the names of column labels and properties.
     *
     * @param clazz <code>Class</code> of the required Bean type
     * @param <T>   the type of Bean
     * @return a <code>ResultSetMapper</code>
     */
    public static <T> ResultSetMapper<T> toBean(@Nonnull Class<T> clazz) {
        return new PropertyBasedResultSetMapper<>(clazz);
    }

    /**
     * Returns a <code>ResultSetMapper</code> that converts columns to {@code Map<String, Object>}
     * whose keys are column label (which is taken from {@link ResultSetMetaData#getColumnLabel(int)})
     * and values are corresponding column values.<br>
     * The property value will be set by the {@link PropertyDescriptor#getWriteMethod()}. It should be
     * a {@code public void} method. Otherwise, the value set won't succeed.
     *
     * @return a <code>ResultSetMapper</code>
     */
    public static ResultSetMapper<Map<String, Object>> toMap() {
        return LabelMapResultSetMapper.getInstance();
    }
}
