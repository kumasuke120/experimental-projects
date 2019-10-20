package app.kumasuke.test.royce.linker;

import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ConnectionHolderTest {
    private final boolean[] invokedRef = {false};
    private Object connectionHolder;

    @BeforeEach
    void init() throws ReflectiveOperationException {
        Class<?> cHClass = Class.forName("app.kumasuke.royce.linker.ConnectionHolder");
        Constructor<?> constructor = cHClass.getDeclaredConstructor(Connection.class);

        Connection mock = mock(Connection.class, mck -> {
            invokedRef[0] = true;
            Class<?> returnType = mck.getMethod().getReturnType();
            return Reflects.getTypeDefaultValue(returnType);
        });

        try {
            constructor.setAccessible(true);
            connectionHolder = constructor.newInstance(mock);
        } finally {
            constructor.setAccessible(false);
        }
    }

    @Test
    void allConnectionMethodsShouldWork() throws ReflectiveOperationException {
        Method getConnMethod = connectionHolder.getClass().getDeclaredMethod("getConnection");
        Connection conn = (Connection) Reflects.invokeWithDefaultArguments(getConnMethod, connectionHolder);

        Reflects.getPublicInstanceMethods(conn)
                .forEach(m -> {
                    try {
                        invokedRef[0] = false;
                        Reflects.invokeWithDefaultArguments(m, conn);
                        assertTrue(invokedRef[0]);
                    } catch (InvocationTargetException e) {
                        if ("close".equals(m.getName()) || "abort".equals(m.getName())) {
                            assertEquals(UnsupportedOperationException.class, e.getTargetException().getClass());
                        } else {
                            throw new AssertionError(e);
                        }
                    }
                });
    }
}
