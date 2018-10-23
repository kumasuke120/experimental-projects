package app.kumasuke.srs.util;

import java.util.Objects;

public class Pair<F, S> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Pair))
            return false;

        Pair p = (Pair) obj;
        return Objects.equals(first, p.first) &&
                Objects.equals(second, p.second);
    }

    @Override
    public String toString() {
        return String.format("Pair(%s, %s)", first, second);
    }
}