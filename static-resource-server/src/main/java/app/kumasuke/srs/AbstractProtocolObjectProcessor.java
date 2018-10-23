package app.kumasuke.srs;

import app.kumasuke.srs.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public abstract class AbstractProtocolObjectProcessor implements ProtocolObjectProcessor {
    protected static final Logger logger = LoggerFactory.getLogger(ProtocolObjectProcessor.class);

    protected final Config config;

    protected AbstractProtocolObjectProcessor(@Nonnull Config config) {
        this.config = config;
    }
}
