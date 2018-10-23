package app.kumasuke.srs.protocol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

class HttpHeaders {
    private final Map<String, List<byte[]>> headers;

    HttpHeaders() {
        headers = new HashMap<>();
    }

    void put(@Nonnull String name, @Nullable byte[] value) {
        final String storeName = name.toLowerCase();
        final List<byte[]> values = headers.computeIfAbsent(storeName, k -> new ArrayList<>());
        values.add(value);
    }

    @Nonnull
    List<byte[]> getValues(@Nonnull String name) {
        final String storeName = name.toLowerCase();
        return Collections.unmodifiableList(headers.getOrDefault(storeName, Collections.emptyList()));
    }

    @Nonnull
    Set<String> getAllNames() {
        final Set<String> names = headers.keySet()
                .stream()
                .map(k -> Arrays.stream(k.split("-"))
                        .filter(s -> !s.isEmpty())
                        .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                        .collect(Collectors.joining("-")))
                .collect(Collectors.toSet());

        return Collections.unmodifiableSet(names);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean containsName(@Nonnull String name) {
        final String storeName = name.toLowerCase();
        return headers.containsKey(storeName);
    }
}
