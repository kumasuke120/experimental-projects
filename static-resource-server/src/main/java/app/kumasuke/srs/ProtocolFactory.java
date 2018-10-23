package app.kumasuke.srs;

import app.kumasuke.srs.util.Config;

import javax.annotation.Nonnull;
import java.nio.channels.SocketChannel;

public interface ProtocolFactory {
    @Nonnull
    ConnectionReader newConnectionReader();

    @Nonnull
    ConnectionWriter newConnectionWriter();

    @Nonnull
    ProtocolObjectProcessor newProtocolObjectProcessor(@Nonnull Config config);
}
