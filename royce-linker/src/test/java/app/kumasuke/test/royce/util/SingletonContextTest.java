package app.kumasuke.test.royce.util;

import app.kumasuke.royce.util.SingletonContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("RedundantStringConstructorCall")
class SingletonContextTest {
    @Test
    void getInstance() {
        SingletonContext<String> singleton = new SingletonContext<>(() -> new String("test"));
        assertEquals("test", singleton.getInstance());
    }

    @Test
    void instanceShouldBeSingleton() {
        assertAll("serial", () -> {
            SingletonContext<String> singleton = new SingletonContext<>(() -> new String("test"));
            String first = singleton.getInstance();
            String second = singleton.getInstance();
            assertSame(first, second);
        });

        assertAll("multi-thread", () -> {
            final SingletonContext<String> singleton = new SingletonContext<>(() -> new String("test"));
            final String[] results = new String[300];
            ExecutorService executor = Executors.newFixedThreadPool(50);
            for (int i = 0; i < results.length; i++) {
                final int index = i;
                executor.execute(() -> results[index] = singleton.getInstance());
            }
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
            if (executor.isTerminated()) {
                final String first = results[0];
                assertEquals("test", first);
                for (int i = 1; i < results.length; i++) {
                    assertSame(first, results[i]);
                }
            } else {
                throw new AssertionError();
            }
        });
    }

    @Test
    void instanceCannotBeNull() {
        final SingletonContext<Object> singleton = new SingletonContext<>(() -> null);
        assertThrows(NullPointerException.class, singleton::getInstance);
    }
}