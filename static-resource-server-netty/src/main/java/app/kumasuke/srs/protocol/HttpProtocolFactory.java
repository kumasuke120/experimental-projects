package app.kumasuke.srs.protocol;

import app.kumasuke.srs.util.Config;
import io.netty.channel.ChannelHandler;

public class HttpProtocolFactory implements ProtocolFactory {
    @Override
    public ChannelHandler newChannelHandler(Config config) {
        return new HttpProtocolChannelHandler(config);
    }
}
