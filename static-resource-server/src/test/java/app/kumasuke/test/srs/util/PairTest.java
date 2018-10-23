package app.kumasuke.test.srs.util;

import app.kumasuke.srs.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairTest {
    @Test
    void createAndGet() {
        final String a = "XXX";
        final String b = "YYY";
        Pair<String, String> pair = new Pair<>(a, b);

        assertEquals(a, pair.getFirst());
        assertEquals(b, pair.getSecond());
    }
}
