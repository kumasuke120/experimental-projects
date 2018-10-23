package app.kumasuke.srs.protocol;

import app.kumasuke.srs.ConnectionReader;
import app.kumasuke.srs.ConnectionWriter;
import app.kumasuke.srs.ProtocolFactory;
import app.kumasuke.srs.ProtocolObjectProcessor;
import app.kumasuke.srs.util.Config;

import javax.annotation.Nonnull;

public class HttpProtocolFactory implements ProtocolFactory {
    @Nonnull
    @Override
    public ConnectionReader newConnectionReader() {
        return new HttpConnectionReader();
    }

    @Nonnull
    @Override
    public ConnectionWriter newConnectionWriter() {
        return new HttpConnectionWriter();
    }

    @Nonnull
    @Override
    public ProtocolObjectProcessor newProtocolObjectProcessor(@Nonnull Config config) {
        return new HttpProtocolObjectProcessor(config);
    }
}
