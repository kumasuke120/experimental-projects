package app.kumasuke.test.royce.util;

import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ReadOnlyResultSetTest {
    private static final String[] unsupportedMethods = {
            "absolute", "afterLast", "beforeFirst", "cancelRowUpdates", "clearWarnings", "close",
            "deleteRow", "first", "insertRow", "last", "moveToCurrentRow", "moveToInsertRow",
            "next", "previous", "refreshRow", "relative", "setFetchDirection", "setFetchSize",
            "updateArray", "updateAsciiStream", "updateBigDecimal", "updateBinaryStream",
            "updateBlob", "updateBoolean", "updateByte", "updateBytes", "updateCharacterStream",
            "updateClob", "updateDate", "updateDouble", "updateFloat", "updateInt", "updateLong",
            "updateNCharacterStream", "updateNClob", "updateNString", "updateNull", "updateObject",
            "updateRef", "updateRow", "updateRowId", "updateSQLXML", "updateShort", "updateString",
            "updateTime", "updateTimestamp"
    };
    private final boolean[] invokedRef = {false};
    private ResultSet resultSet;

    @BeforeEach
    void init() throws ReflectiveOperationException {
        Class<?> rsClass = Class.forName("app.kumasuke.royce.util.Statements$ReadOnlyResultSet");
        Constructor<?> constructor = rsClass.getDeclaredConstructor(ResultSet.class);

        ResultSet mock = mock(ResultSet.class, mck -> {
            invokedRef[0] = true;
            Class<?> returnType = mck.getMethod().getReturnType();
            return Reflects.getTypeDefaultValue(returnType);
        });

        try {
            constructor.setAccessible(true);
            resultSet = (ResultSet) constructor.newInstance(mock);
        } finally {
            constructor.setAccessible(false);
        }
    }

    @Test
    void allUpdateMethodsShouldBeUnsupported() {
        Reflects.getPublicInstanceMethods(resultSet)
                .forEach(m -> {
                    try {
                        invokedRef[0] = false;
                        Reflects.invokeWithDefaultArguments(m, resultSet);
                        assertTrue(invokedRef[0], m.toString());
                    } catch (InvocationTargetException e) {
                        assertTrue(e.getTargetException() instanceof UnsupportedOperationException);
                        assertTrue(Arrays.asList(unsupportedMethods).contains(m.getName()));
                    }
                });
    }
}
