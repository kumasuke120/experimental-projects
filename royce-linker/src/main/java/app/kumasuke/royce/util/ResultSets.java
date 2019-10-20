package app.kumasuke.royce.util;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.royce.util.JdbcObjectValueGetHelper.ResultSetValueGetSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Stream;

public class ResultSets {
    private ResultSets() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static Object getValue(@Nonnull ResultSet resultSet, int index, @Nonnull Class<?> javaType)
            throws SQLException {
        ResultSetValueGetSupport valueGettable = new ResultSetValueGetSupport(resultSet);
        return JdbcObjectValueGetHelper.getValue(valueGettable, index, javaType);
    }

    static <T> Optional<T> mapResultOne(ResultSet rs, ResultSetMapper<T> mapper) throws SQLException {
        ReadOnlyResultSet rrs = new ReadOnlyResultSet(rs);

        if (rs.next()) {
            T retVal = mapper.mapRow(rrs);
            if (rs.next()) {
                throw new IllegalStateException("SQL returns more than 1 row");
            } else {
                return retVal == null ? Optional.empty() : Optional.of(retVal);
            }
        } else {
            return Optional.empty();
        }
    }

    static <T> Stream<T> mapResultMany(ResultSet rs, ResultSetMapper<T> mapper) throws SQLException {
        ReadOnlyResultSet rrs = new ReadOnlyResultSet(rs);

        List<T> ts = new LinkedList<>();
        while (rs.next()) {
            T t = mapper.mapRow(rrs);
            ts.add(t);
        }

        return Collections.unmodifiableList(ts)
                .stream();
    }

    private static class ReadOnlyResultSet implements ResultSet {
        private final ResultSet resultSet;

        ReadOnlyResultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public boolean next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean wasNull() throws SQLException {
            return resultSet.wasNull();
        }

        @Override
        public String getString(int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex);
        }

        @Override
        public boolean getBoolean(int columnIndex) throws SQLException {
            return resultSet.getBoolean(columnIndex);
        }

        @Override
        public byte getByte(int columnIndex) throws SQLException {
            return resultSet.getByte(columnIndex);
        }

        @Override
        public short getShort(int columnIndex) throws SQLException {
            return resultSet.getShort(columnIndex);
        }

        @Override
        public int getInt(int columnIndex) throws SQLException {
            return resultSet.getInt(columnIndex);
        }

        @Override
        public long getLong(int columnIndex) throws SQLException {
            return resultSet.getLong(columnIndex);
        }

        @Override
        public float getFloat(int columnIndex) throws SQLException {
            return resultSet.getFloat(columnIndex);
        }

        @Override
        public double getDouble(int columnIndex) throws SQLException {
            return resultSet.getDouble(columnIndex);
        }

        @Override
        @Deprecated
        public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            return resultSet.getBigDecimal(columnIndex, scale);
        }

        @Override
        public byte[] getBytes(int columnIndex) throws SQLException {
            return resultSet.getBytes(columnIndex);
        }

        @Override
        public Date getDate(int columnIndex) throws SQLException {
            return resultSet.getDate(columnIndex);
        }

        @Override
        public Time getTime(int columnIndex) throws SQLException {
            return resultSet.getTime(columnIndex);
        }

        @Override
        public Timestamp getTimestamp(int columnIndex) throws SQLException {
            return resultSet.getTimestamp(columnIndex);
        }

        @Override
        public InputStream getAsciiStream(int columnIndex) throws SQLException {
            return resultSet.getAsciiStream(columnIndex);
        }

        @Override
        @Deprecated
        public InputStream getUnicodeStream(int columnIndex) throws SQLException {
            return resultSet.getUnicodeStream(columnIndex);
        }

        @Override
        public InputStream getBinaryStream(int columnIndex) throws SQLException {
            return resultSet.getBinaryStream(columnIndex);
        }

        @Override
        public String getString(String columnLabel) throws SQLException {
            return resultSet.getString(columnLabel);
        }

        @Override
        public boolean getBoolean(String columnLabel) throws SQLException {
            return resultSet.getBoolean(columnLabel);
        }

        @Override
        public byte getByte(String columnLabel) throws SQLException {
            return resultSet.getByte(columnLabel);
        }

        @Override
        public short getShort(String columnLabel) throws SQLException {
            return resultSet.getShort(columnLabel);
        }

        @Override
        public int getInt(String columnLabel) throws SQLException {
            return resultSet.getInt(columnLabel);
        }

        @Override
        public long getLong(String columnLabel) throws SQLException {
            return resultSet.getLong(columnLabel);
        }

        @Override
        public float getFloat(String columnLabel) throws SQLException {
            return resultSet.getFloat(columnLabel);
        }

        @Override
        public double getDouble(String columnLabel) throws SQLException {
            return resultSet.getDouble(columnLabel);
        }

        @Override
        @Deprecated
        public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
            return resultSet.getBigDecimal(columnLabel, scale);
        }

        @Override
        public byte[] getBytes(String columnLabel) throws SQLException {
            return resultSet.getBytes(columnLabel);
        }

        @Override
        public Date getDate(String columnLabel) throws SQLException {
            return resultSet.getDate(columnLabel);
        }

        @Override
        public Time getTime(String columnLabel) throws SQLException {
            return resultSet.getTime(columnLabel);
        }

        @Override
        public Timestamp getTimestamp(String columnLabel) throws SQLException {
            return resultSet.getTimestamp(columnLabel);
        }

        @Override
        public InputStream getAsciiStream(String columnLabel) throws SQLException {
            return resultSet.getAsciiStream(columnLabel);
        }

        @Override
        @Deprecated
        public InputStream getUnicodeStream(String columnLabel) throws SQLException {
            return resultSet.getUnicodeStream(columnLabel);
        }

        @Override
        public InputStream getBinaryStream(String columnLabel) throws SQLException {
            return resultSet.getBinaryStream(columnLabel);
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return resultSet.getWarnings();
        }

        @Override
        public void clearWarnings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCursorName() throws SQLException {
            return resultSet.getCursorName();
        }

        @Override
        public ResultSetMetaData getMetaData() throws SQLException {
            return resultSet.getMetaData();
        }

        @Override
        public Object getObject(int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex);
        }

        @Override
        public Object getObject(String columnLabel) throws SQLException {
            return resultSet.getObject(columnLabel);
        }

        @Override
        public int findColumn(String columnLabel) throws SQLException {
            return resultSet.findColumn(columnLabel);
        }

        @Override
        public Reader getCharacterStream(int columnIndex) throws SQLException {
            return resultSet.getCharacterStream(columnIndex);
        }

        @Override
        public Reader getCharacterStream(String columnLabel) throws SQLException {
            return resultSet.getCharacterStream(columnLabel);
        }

        @Override
        public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            return resultSet.getBigDecimal(columnIndex);
        }

        @Override
        public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
            return resultSet.getBigDecimal(columnLabel);
        }

        @Override
        public boolean isBeforeFirst() throws SQLException {
            return resultSet.isBeforeFirst();
        }

        @Override
        public boolean isAfterLast() throws SQLException {
            return resultSet.isAfterLast();
        }

        @Override
        public boolean isFirst() throws SQLException {
            return resultSet.isFirst();
        }

        @Override
        public boolean isLast() throws SQLException {
            return resultSet.isLast();
        }

        @Override
        public void beforeFirst() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void afterLast() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean first() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean last() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRow() throws SQLException {
            return resultSet.getRow();
        }

        @Override
        public boolean absolute(int row) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean relative(int rows) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean previous() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFetchDirection() throws SQLException {
            return resultSet.getFetchDirection();
        }

        @Override
        public void setFetchDirection(int direction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFetchSize() throws SQLException {
            return resultSet.getFetchSize();
        }

        @Override
        public void setFetchSize(int rows) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getType() throws SQLException {
            return resultSet.getType();
        }

        @Override
        public int getConcurrency() throws SQLException {
            return resultSet.getConcurrency();
        }

        @Override
        public boolean rowUpdated() throws SQLException {
            return resultSet.rowUpdated();
        }

        @Override
        public boolean rowInserted() throws SQLException {
            return resultSet.rowInserted();
        }

        @Override
        public boolean rowDeleted() throws SQLException {
            return resultSet.rowDeleted();
        }

        @Override
        public void updateNull(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBoolean(int columnIndex, boolean x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateByte(int columnIndex, byte x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateShort(int columnIndex, short x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateInt(int columnIndex, int x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateLong(int columnIndex, long x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateFloat(int columnIndex, float x) throws SQLException {
            resultSet.updateFloat(columnIndex, x);
        }

        @Override
        public void updateDouble(int columnIndex, double x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBigDecimal(int columnIndex, BigDecimal x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateString(int columnIndex, String x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBytes(int columnIndex, byte[] x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDate(int columnIndex, Date x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTime(int columnIndex, Time x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTimestamp(int columnIndex, Timestamp x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(int columnIndex, InputStream x, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(int columnIndex, InputStream x, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(int columnIndex, Reader x, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(int columnIndex, Object x, int scaleOrLength) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(int columnIndex, Object x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNull(String columnLabel) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBoolean(String columnLabel, boolean x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateByte(String columnLabel, byte x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateShort(String columnLabel, short x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateInt(String columnLabel, int x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateLong(String columnLabel, long x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateFloat(String columnLabel, float x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDouble(String columnLabel, double x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBigDecimal(String columnLabel, BigDecimal x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateString(String columnLabel, String x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBytes(String columnLabel, byte[] x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDate(String columnLabel, Date x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTime(String columnLabel, Time x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTimestamp(String columnLabel, Timestamp x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(String columnLabel, InputStream x, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(String columnLabel, InputStream x, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(String columnLabel, Reader reader, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(String columnLabel, Object x, int scaleOrLength) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(String columnLabel, Object x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insertRow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteRow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void refreshRow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cancelRowUpdates() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void moveToInsertRow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void moveToCurrentRow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Statement getStatement() throws SQLException {
            return resultSet.getStatement();
        }

        @Override
        public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
            return resultSet.getObject(columnIndex, map);
        }

        @Override
        public Ref getRef(int columnIndex) throws SQLException {
            return resultSet.getRef(columnIndex);
        }

        @Override
        public Blob getBlob(int columnIndex) throws SQLException {
            return resultSet.getBlob(columnIndex);
        }

        @Override
        public Clob getClob(int columnIndex) throws SQLException {
            return resultSet.getClob(columnIndex);
        }

        @Override
        public Array getArray(int columnIndex) throws SQLException {
            return resultSet.getArray(columnIndex);
        }

        @Override
        public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
            return resultSet.getObject(columnLabel, map);
        }

        @Override
        public Ref getRef(String columnLabel) throws SQLException {
            return resultSet.getRef(columnLabel);
        }

        @Override
        public Blob getBlob(String columnLabel) throws SQLException {
            return resultSet.getBlob(columnLabel);
        }

        @Override
        public Clob getClob(String columnLabel) throws SQLException {
            return resultSet.getClob(columnLabel);
        }

        @Override
        public Array getArray(String columnLabel) throws SQLException {
            return resultSet.getArray(columnLabel);
        }

        @Override
        public Date getDate(int columnIndex, Calendar cal) throws SQLException {
            return resultSet.getDate(columnIndex, cal);
        }

        @Override
        public Date getDate(String columnLabel, Calendar cal) throws SQLException {
            return resultSet.getDate(columnLabel, cal);
        }

        @Override
        public Time getTime(int columnIndex, Calendar cal) throws SQLException {
            return resultSet.getTime(columnIndex, cal);
        }

        @Override
        public Time getTime(String columnLabel, Calendar cal) throws SQLException {
            return resultSet.getTime(columnLabel, cal);
        }

        @Override
        public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            return resultSet.getTimestamp(columnIndex, cal);
        }

        @Override
        public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
            return resultSet.getTimestamp(columnLabel, cal);
        }

        @Override
        public URL getURL(int columnIndex) throws SQLException {
            return resultSet.getURL(columnIndex);
        }

        @Override
        public URL getURL(String columnLabel) throws SQLException {
            return resultSet.getURL(columnLabel);
        }

        @Override
        public void updateRef(int columnIndex, Ref x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRef(String columnLabel, Ref x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(int columnIndex, Blob x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(String columnLabel, Blob x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(int columnIndex, Clob x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(String columnLabel, Clob x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateArray(int columnIndex, Array x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateArray(String columnLabel, Array x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RowId getRowId(int columnIndex) throws SQLException {
            return resultSet.getRowId(columnIndex);
        }

        @Override
        public RowId getRowId(String columnLabel) throws SQLException {
            return resultSet.getRowId(columnLabel);
        }

        @Override
        public void updateRowId(int columnIndex, RowId x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRowId(String columnLabel, RowId x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getHoldability() throws SQLException {
            return resultSet.getHoldability();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return resultSet.isClosed();
        }

        @Override
        public void updateNString(int columnIndex, String nString) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNString(String columnLabel, String nString) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(int columnIndex, NClob nClob) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(String columnLabel, NClob nClob) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NClob getNClob(int columnIndex) throws SQLException {
            return resultSet.getNClob(columnIndex);
        }

        @Override
        public NClob getNClob(String columnLabel) throws SQLException {
            return resultSet.getNClob(columnLabel);
        }

        @Override
        public SQLXML getSQLXML(int columnIndex) throws SQLException {
            return resultSet.getSQLXML(columnIndex);
        }

        @Override
        public SQLXML getSQLXML(String columnLabel) throws SQLException {
            return resultSet.getSQLXML(columnLabel);
        }

        @Override
        public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateSQLXML(String columnLabel, SQLXML xmlObject) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getNString(int columnIndex) throws SQLException {
            return resultSet.getNString(columnIndex);
        }

        @Override
        public String getNString(String columnLabel) throws SQLException {
            return resultSet.getNString(columnLabel);
        }

        @Override
        public Reader getNCharacterStream(int columnIndex) throws SQLException {
            return resultSet.getNCharacterStream(columnIndex);
        }

        @Override
        public Reader getNCharacterStream(String columnLabel) throws SQLException {
            return resultSet.getNCharacterStream(columnLabel);
        }

        @Override
        public void updateNCharacterStream(int columnIndex, Reader x, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(String columnLabel, Reader reader, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(int columnIndex, InputStream x, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(int columnIndex, InputStream x, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(int columnIndex, Reader x, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(String columnLabel, InputStream x, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(String columnLabel, InputStream x, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(String columnLabel, Reader reader, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(int columnIndex, InputStream inputStream, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(String columnLabel, InputStream inputStream, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(int columnIndex, Reader reader, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(String columnLabel, Reader reader, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(int columnIndex, Reader reader, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(String columnLabel, Reader reader, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(int columnIndex, Reader x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(String columnLabel, Reader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(int columnIndex, InputStream x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(int columnIndex, InputStream x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(int columnIndex, Reader x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(String columnLabel, InputStream x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(String columnLabel, InputStream x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(String columnLabel, Reader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(int columnIndex, InputStream inputStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(String columnLabel, InputStream inputStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(int columnIndex, Reader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(String columnLabel, Reader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(int columnIndex, Reader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(String columnLabel, Reader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            return resultSet.getObject(columnIndex, type);
        }

        @Override
        public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
            return resultSet.getObject(columnLabel, type);
        }

        @Override
        public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(int columnIndex, Object x, SQLType targetSqlType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(String columnLabel, Object x, SQLType targetSqlType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return resultSet.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return resultSet.isWrapperFor(iface);
        }
    }
}
