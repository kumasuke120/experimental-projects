package app.kumasuke.test.royce.linker;

import app.kumasuke.royce.linker.Linkers;
import app.kumasuke.test.util.Reflects;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkersTest {
    @Test
    void linkersCannotBeInstantiated() {
        // new Linkers();

        InvocationTargetException e = assertThrows(InvocationTargetException.class,
                                                   () -> Reflects.newInstance(Linkers.class));
        assertTrue(e.getCause() instanceof UnsupportedOperationException);
    }
}
