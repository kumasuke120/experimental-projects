package app.kumasuke.srs.protocol;

import app.kumasuke.srs.util.Config;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

class HttpProtocolChannelHandler extends ChannelInitializer<SocketChannel> {
    private static final int _2_MIB = 2 * 1024 * 1028;

    private final Config config;

    HttpProtocolChannelHandler(Config config) {
        this.config = config;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("httpServerCodec", new HttpServerCodec())
                .addLast("HttpObjectAggregator", new HttpObjectAggregator(_2_MIB))
                .addLast("HttpStaticResourceProcessor", new HttpStaticResourceProcessor(config));
    }
}
