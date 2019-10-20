package app.kumasuke.test.royce.mapper;

import app.kumasuke.royce.mapper.ResultSetMapper;
import app.kumasuke.test.util.Book;
import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PropertyBasedResultSetMapperTest {
    private ResultSet resultSet;

    @BeforeEach
    void init() throws SQLException {
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(metaData.getTableName(anyInt())).thenReturn("books");
        when(metaData.getColumnCount()).thenReturn(10);

        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");
        when(metaData.getColumnLabel(3)).thenReturn("author");
        when(metaData.getColumnLabel(4)).thenReturn("publisher");
        when(metaData.getColumnLabel(5)).thenReturn("price");
        when(metaData.getColumnLabel(6)).thenReturn("language");
        when(metaData.getColumnLabel(7)).thenReturn("pages");
        when(metaData.getColumnLabel(8)).thenReturn("isbn");
        when(metaData.getColumnLabel(9)).thenReturn("release_date");
        when(metaData.getColumnLabel(10)).thenReturn("nothing");

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getMetaData()).thenReturn(metaData);

        when(resultSet.getInt(1)).thenReturn(5);
        when(resultSet.getInt("id")).thenReturn(5);
        when(resultSet.getObject(1, Integer.class)).thenReturn(5);
        when(resultSet.getObject("id", Integer.class)).thenReturn(5);

        when(resultSet.getString(2)).thenReturn("The Fault in Our Stars");
        when(resultSet.getObject(2, String.class)).thenReturn("The Fault in Our Stars");
        when(resultSet.getString("name")).thenReturn("The Fault in Our Stars");
        when(resultSet.getObject("name", String.class)).thenReturn("The Fault in Our Stars");

        when(resultSet.getString(3)).thenReturn("John Green");
        when(resultSet.getObject(3, String.class)).thenReturn("John Green");
        when(resultSet.getString("author")).thenReturn("John Green");
        when(resultSet.getObject("author", String.class)).thenReturn("John Green");

        when(resultSet.getString(4))
                .thenReturn("Penguin Books; Reprint edition (April 8, 2014)");
        when(resultSet.getObject(4, String.class))
                .thenReturn("Penguin Books; Reprint edition (April 8, 2014)");
        when(resultSet.getString("publisher"))
                .thenReturn("Penguin Books; Reprint edition (April 8, 2014)");
        when(resultSet.getObject("publisher", String.class))
                .thenReturn("Penguin Books; Reprint edition (April 8, 2014)");

        when(resultSet.getBigDecimal(5)).thenReturn(new BigDecimal("7.27"));
        when(resultSet.getObject(5, BigDecimal.class))
                .thenReturn(new BigDecimal("7.27"));
        when(resultSet.getBigDecimal("price")).thenReturn(new BigDecimal("7.27"));
        when(resultSet.getObject("price", BigDecimal.class))
                .thenReturn(new BigDecimal("7.27"));

        when(resultSet.getString(6)).thenReturn("English");
        when(resultSet.getObject(6, String.class)).thenReturn("English");
        when(resultSet.getString("language")).thenReturn("English");
        when(resultSet.getObject("language", String.class)).thenReturn("English");

        when(resultSet.getInt(7)).thenReturn(352);
        when(resultSet.getObject(7, int.class)).thenReturn(352);
        when(resultSet.getInt("pages")).thenReturn(352);
        when(resultSet.getObject("pages", int.class)).thenReturn(352);

        when(resultSet.getString(8)).thenReturn("978-0142424179");
        when(resultSet.getObject(8, String.class)).thenReturn("978-0142424179");
        when(resultSet.getString("isbn")).thenReturn("978-0142424179");
        when(resultSet.getObject("isbn", String.class)).thenReturn("978-0142424179");

        when(resultSet.getObject(9, LocalDate.class))
                .thenReturn(LocalDate.of(2014, 4, 8));
        when(resultSet.getObject("release_date", LocalDate.class))
                .thenReturn(LocalDate.of(2014, 4, 8));

        this.resultSet = resultSet;
    }

    @Test
    void nonPublicBeanClassShouldNotBeInstantiate() throws InvocationTargetException {
        class C {
            private C() {

            }
        }

        final ResultSetMapper<C> mapper = newPropertyBasedResultSetMapper(C.class);
        assertThrows(IllegalStateException.class, () -> mapper.mapRow(resultSet));
    }

    @Test
    void mapRow() throws SQLException, InvocationTargetException {
        Book expected = Book.mapper().mapRow(resultSet);
        ResultSetMapper<Book> mapper = newPropertyBasedResultSetMapper(Book.class);
        Book actual = mapper.mapRow(resultSet);
        assertEquals(expected, actual);
    }

    @Test
    void mapRowShouldThrowWhenIllegalAccess() throws InvocationTargetException {
        final ResultSetMapper<ObjectBean> mapper = newPropertyBasedResultSetMapper(ObjectBean.class);
        assertThrows(IllegalArgumentException.class, () -> mapper.mapRow(resultSet));
    }

    private <T> ResultSetMapper<T> newPropertyBasedResultSetMapper(Class<T> beanClass)
            throws InvocationTargetException {
        return Reflects.newInstance(
                "app.kumasuke.royce.mapper.PropertyBasedResultSetMapper", beanClass);
    }

    @SuppressWarnings("unused")
    public static class ObjectBean {
        private Integer id;

        public Integer getId() {
            return id;
        }

        void setId(Integer id) {
            this.id = id;
        }
    }
}
