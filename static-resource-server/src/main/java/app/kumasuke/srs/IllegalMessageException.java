package app.kumasuke.srs;

import javax.annotation.Nonnull;

public class IllegalMessageException extends RuntimeException {
    public IllegalMessageException(@Nonnull String message) {
        super(message);
    }
}
