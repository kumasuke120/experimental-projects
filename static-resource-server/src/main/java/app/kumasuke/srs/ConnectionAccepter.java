package app.kumasuke.srs;

import app.kumasuke.srs.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

class ConnectionAccepter {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionAccepter.class);

    private final Config config;
    private final ServerSocketChannel serverSocketChannel;
    private final BlockingQueue<Connection> connectionQueue;

    private Thread accepterThread;
    private volatile boolean isRunning = false;

    ConnectionAccepter(@Nonnull Config config,
                       @Nonnull ServerSocketChannel serverSocketChannel,
                       @Nonnull BlockingQueue<Connection> connectionQueue) {
        this.config = config;
        this.serverSocketChannel = serverSocketChannel;
        this.connectionQueue = connectionQueue;
    }

    void start() {
        accepterThread = new AcceptThread();
        accepterThread.start();
        isRunning = true;

        logger.debug("ConnectionAccepter started");
    }

    void stop() {
        isRunning = false;
        accepterThread = null;

        logger.debug("ConnectionAccepter stopped");
    }

    private class AcceptThread extends Thread {
        private long currentConnectionId = 0L;

        AcceptThread() {
            super(config.getServerNameWithoutVersion() + "-ConnectionAccepter");
        }

        @Override
        public void run() {
            while (isRunning) {
                final SocketChannel socketChannel;
                final SocketAddress remoteAddress;
                final SocketAddress localAddress;

                try {
                    socketChannel = serverSocketChannel.accept();
                    remoteAddress = socketChannel.getRemoteAddress();
                    localAddress = socketChannel.getLocalAddress();
                    socketChannel.configureBlocking(false);
                } catch (IOException e) {
                    logger.error("error encountered when accepting socket", e);
                    continue;
                }

                final var conn = new Connection(++currentConnectionId, socketChannel);
                try {
                    connectionQueue.put(conn);

                    logger.info("a new connection built: Connection#{}({} -> {})", conn.id(),
                                remoteAddress, localAddress);
                } catch (InterruptedException e) {
                    ConnectionAccepter.this.stop();
                }
            }
        }
    }
}
