package app.kumasuke.test.srs.util;

import app.kumasuke.srs.util.DynamicByteBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicByteBufferTest {
    private static Random rand;

    @BeforeAll
    static void initAll() {
        rand = new SecureRandom();
    }

    private static byte[] nextByteArray(int length) {
        final var bytes = new byte[length];
        rand.nextBytes(bytes);
        return bytes;
    }

    private static String nextString(int length) {
        final char[] availableChars = {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        };
        final var builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final char c = availableChars[rand.nextInt(availableChars.length)];
            builder.append(c);
        }
        return builder.toString();
    }

    @ParameterizedTest
    @ValueSource(ints = {512, 3072, 4096})
    void create(final int length) {
        final var buffer = new DynamicByteBuffer();
        assertArrayEquals(new byte[0], buffer.toByteArray());

        final var tmpByteArray = nextByteArray(length);
        final var buffer2 = new DynamicByteBuffer(tmpByteArray);
        assertArrayEquals(tmpByteArray, buffer2.toByteArray());
    }

    @Test
    void lengthAndIsEmpty() {
        final var buffer = new DynamicByteBuffer();
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.length());

        final int length = rand.nextInt(4096) + 2;
        final var bytes = nextByteArray(length);
        buffer.append(bytes);
        assertFalse(buffer.isEmpty());
        assertEquals(length, buffer.length());

        final int popLength = length / 2;
        buffer.pop(popLength);
        assertFalse(buffer.isEmpty());
        assertEquals(length - popLength, buffer.length());
    }

    @Test
    void append() {
        final int length1 = rand.nextInt(500) + 2048;
        final int length2 = rand.nextInt(2048);

        final var bytes1 = nextByteArray(length1);
        final var bytes2 = nextByteArray(length2);
        final var expected = new byte[length1 + length2];
        System.arraycopy(bytes1, 0, expected, 0, length1);
        System.arraycopy(bytes2, 0, expected, length1, length2);

        final var buffer = new DynamicByteBuffer();
        buffer.append(bytes1);
        buffer.append(bytes2);

        assertArrayEquals(expected, buffer.toByteArray());

        final byte[] pop = buffer.pop(length1);
        assertArrayEquals(bytes1, pop);
        assertArrayEquals(bytes2, buffer.toByteArray());
    }

    @RepeatedTest(10)
    void get() {
        final var expected = nextByteArray(4096);
        final var buffer = new DynamicByteBuffer(expected);
        assertAll("getByIndex", () -> {
            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], buffer.get(i));
            }

            assertThrows(IndexOutOfBoundsException.class, () -> buffer.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> buffer.get(4096));
        });

        assertAll("getByRange", () -> {
            final int start = rand.nextInt(expected.length - 1);
            final int length = rand.nextInt(expected.length - start - 1) + 1;

            final byte[] actual = buffer.get(start, length);
            for (int i = 0; i < length; i++) {
                assertEquals(buffer.get(start + i), actual[i]);
            }

            assertThrows(IndexOutOfBoundsException.class, () -> buffer.get(4096, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> buffer.get(-1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> buffer.get(4095, 2));
            assertThrows(IllegalArgumentException.class, () -> buffer.get(4095, 0));
            assertThrows(IllegalArgumentException.class, () -> buffer.get(4095, -1));
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {512, 3072, 4096})
    void pop(int popLength) {
        final var expected = nextByteArray(4096);

        final var buffer = new DynamicByteBuffer(expected);
        final var expectedRange = new byte[popLength];
        System.arraycopy(expected, 0, expectedRange, 0, expectedRange.length);
        assertArrayEquals(expectedRange, buffer.pop(expectedRange.length));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 400, 600, 800, 1000})
    void toString(int length) {
        final String str = nextString(length);
        final byte[] sBytes = str.getBytes();
        final var buffer = new DynamicByteBuffer(sBytes);
        assertEquals(str, buffer.toString());

        final Charset charset = Charset.forName("UTF-8");
        final String strUtf8 = str + "汉字";
        final byte[] sUtf8Bytes = strUtf8.getBytes(charset);
        final var buffer2 = new DynamicByteBuffer(sUtf8Bytes);
        assertEquals(strUtf8, buffer2.toString(charset));
    }

    @Test
    void toByteArray() {
        final var expected = nextByteArray(4096);
        final var buffer = new DynamicByteBuffer(expected);
        final byte[] bytes = buffer.toByteArray();
        for (int i = 0; i < buffer.length(); i++) {
            assertEquals(buffer.get(i), bytes[i]);
        }
        assertArrayEquals(expected, bytes);
    }

    @Test
    void clear() {
        final var expected = nextByteArray(4096);
        final var buffer = new DynamicByteBuffer(expected);
        buffer.clear();

        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.length());
        assertArrayEquals(new byte[0], buffer.toByteArray());

        buffer.append(expected);

        final byte[] bytes = buffer.toByteArray();
        assertArrayEquals(expected, bytes);
    }
}
