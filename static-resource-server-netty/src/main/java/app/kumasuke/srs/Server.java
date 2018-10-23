package app.kumasuke.srs;

import app.kumasuke.srs.protocol.ProtocolFactory;
import app.kumasuke.srs.util.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    private static final int DEFAULT_SOCKET_BACKLOG = 1024;

    private final Config config;
    private final ProtocolFactory protocolFactory;
    private final EventLoopGroup parentGroup;
    private final EventLoopGroup childGroup;

    private ChannelFuture channelFuture;

    public Server(Config config, ProtocolFactory protocolFactory) {
        this.config = config;
        this.protocolFactory = protocolFactory;
        this.parentGroup = new NioEventLoopGroup();
        this.childGroup = new NioEventLoopGroup();
    }

    public void start() {
        final var bootstrap = new ServerBootstrap()
                .group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(protocolFactory.newChannelHandler(config))
                .option(ChannelOption.SO_BACKLOG, DEFAULT_SOCKET_BACKLOG)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        final int serverPort = config.getServerPort();
        channelFuture = bootstrap.bind(serverPort).syncUninterruptibly();
    }

    public void stop() {
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();

        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignore) {
            // ignores
        }
    }
}
