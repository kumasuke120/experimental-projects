package app.kumasuke.test.royce.util;

import app.kumasuke.royce.util.TypeConverter;
import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeConverterTest {
    @Test
    void typeConverterCannotBeInstantiated() {
        // new TypeConverter();

        InvocationTargetException e =
                assertThrows(InvocationTargetException.class,
                             () -> Reflects.newInstance(TypeConverter.class));
        assertTrue(e.getCause() instanceof UnsupportedOperationException);
    }
}
