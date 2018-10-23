package app.kumasuke.srs;

import javax.annotation.Nonnull;

public interface ProtocolObjectProcessor {
    @Nonnull
    Object process(@Nonnull Object object);
}
