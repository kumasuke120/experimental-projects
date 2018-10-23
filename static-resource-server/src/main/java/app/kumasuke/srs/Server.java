package app.kumasuke.srs;

import app.kumasuke.srs.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final Config config;
    private final ProtocolFactory protocolFactory;

    private ServerSocketChannel serverSocketChannel;
    private BlockingQueue<Connection> connectionQueue;
    private ConnectionAccepter accepter;
    private ConnectionProcessor processor;

    public Server(@Nonnull Config config, @Nonnull ProtocolFactory protocolFactory) {
        this.config = config;
        this.protocolFactory = protocolFactory;
    }

    public void start() {
        openServerSocketChannel();

        connectionQueue = new LinkedBlockingQueue<>();
        accepter = new ConnectionAccepter(config, serverSocketChannel, connectionQueue);
        processor = new ConnectionProcessor(config, protocolFactory, connectionQueue);

        accepter.start();
        processor.start();

        logger.info("server started on port: " + config.getServerPort());
    }

    public void stop() {
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            logger.warn("error encountered when open closing server", e);
        }

        accepter.stop();
        accepter = null;

        processor.stop();
        processor = null;

        connectionQueue = null;

        logger.info("server stopped");
    }

    private void openServerSocketChannel() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(config.getServerPort()));
        } catch (IOException e) {
            logger.error("cannot start server due to some errors", e);
        }
    }
}
