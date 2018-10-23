package app.kumasuke.srs.protocol;

import app.kumasuke.srs.util.Config;
import io.netty.channel.ChannelHandler;

public interface ProtocolFactory {
    ChannelHandler newChannelHandler(Config config);
}
