package app.kumasuke.srs;

import app.kumasuke.srs.util.Config;
import app.kumasuke.srs.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class ConnectionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionProcessor.class);

    private static final int MIN_IO_THREAD_COUNT = 2;
    private static final int IO_THREAD_COUNT = Math.max(MIN_IO_THREAD_COUNT,
                                                        Runtime.getRuntime().availableProcessors());
    private static final int CORE_WORKER_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 8;
    private static final int MAX_WORKER_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 64 + 1;

    private final Config config;
    private final ProtocolFactory protocolFactory;
    private final ProtocolObjectProcessor protocolObjectProcessor;
    private final BlockingQueue<Connection> connectionQueue;
    private final List<Queue<Connection>> inboundQueues;
    private final List<Queue<Connection>> outboundQueues;

    private Thread[] ioThreads;
    private ExecutorService workerThreads;
    private Thread dispatcherThread;

    private volatile boolean isRunning = false;

    ConnectionProcessor(@Nonnull Config config,
                        @Nonnull ProtocolFactory protocolFactory,
                        @Nonnull BlockingQueue<Connection> connectionQueue) {
        this.config = config;
        this.protocolFactory = protocolFactory;
        this.protocolObjectProcessor = protocolFactory.newProtocolObjectProcessor(config);
        this.connectionQueue = connectionQueue;
        this.inboundQueues = createIOQueues();
        this.outboundQueues = createIOQueues();
    }

    private List<Queue<Connection>> createIOQueues() {
        final var tmp = new ArrayList<Queue<Connection>>();
        for (int i = 0; i < IO_THREAD_COUNT; i++) {
            tmp.add(new ConcurrentLinkedQueue<>());
        }
        return Collections.unmodifiableList(tmp);
    }

    void start() {
        isRunning = true;

        dispatcherThread = new ConnectionDispatcherThread();
        dispatcherThread.start();

        ioThreads = new Thread[IO_THREAD_COUNT];
        for (int i = 0; i < IO_THREAD_COUNT; i++) {
            try {
                ioThreads[i] = new IOThread(i);
                ioThreads[i].start();
            } catch (IOException e) {
                logger.error("cannot create io thread", e);
                isRunning = false;
                return;
            }
        }

        this.workerThreads = new ThreadPoolExecutor(CORE_WORKER_THREAD_COUNT, MAX_WORKER_THREAD_COUNT,
                                                    60L, TimeUnit.SECONDS,
                                                    new LinkedBlockingQueue<>(),
                                                    new WorkerThreadFactory());

        logger.debug("ConnectionProcessor started");
    }

    void stop() {
        isRunning = false;
        if (dispatcherThread.isAlive()) {
            dispatcherThread.interrupt();
            dispatcherThread = null;
        }

        for (int i = 0; i < IO_THREAD_COUNT; i++) {
            if (ioThreads[i].isAlive()) {
                ioThreads[i].interrupt();
                ioThreads[i] = null;
            }
        }

        workerThreads.shutdownNow();
        workerThreads = null;

        logger.debug("ConnectionProcessor stopped");
    }

    private int getIOThreadIndex(Connection conn) {
        return (int) (Math.abs(conn.id()) % IO_THREAD_COUNT);
    }

    private Future<Object> submitProtocolObjectProcessTask(Object protocolObject) {
        final var task = new ProtocolObjectProcessTask(protocolObject);
        return workerThreads.submit(task);
    }

    private class ConnectionDispatcherThread extends Thread {
        ConnectionDispatcherThread() {
            super(config.getServerNameWithoutVersion() + "-ConnectionDispatcher");
        }

        @Override
        public void run() {
            while (isRunning) {
                final Connection conn;
                try {
                    conn = connectionQueue.take();
                } catch (InterruptedException e) {
                    isRunning = false;
                    break;
                }

                final int ioThreadIndex = getIOThreadIndex(conn);
                inboundQueues.get(ioThreadIndex).add(conn);
                logger.debug("connection has dispatched to io thread: Connection#{} -> IO#{}",
                             conn.id(), ioThreadIndex);
            }
        }
    }

    private class IOThread extends Thread {
        private static final int MAX_POLL_SIZE = 8;

        private final int index;
        private final Selector readSelector;
        private final Selector writeSelector;
        private final Map<Long, ConnectionReader> readers;
        private final Map<Long, ConnectionWriter> writers;
        private final List<Pair<Connection, Future<Object>>> submittedTasks;

        IOThread(int index) throws IOException {
            super(config.getServerNameWithoutVersion() + "-IO-" + index);
            this.index = index;
            this.readSelector = Selector.open();
            this.writeSelector = Selector.open();
            this.readers = new HashMap<>();
            this.writers = new HashMap<>();
            this.submittedTasks = new LinkedList<>();
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    pollFromInboundQueue();
                    readFromSelectedConnections();

                    checkSubmittedTasks();

                    pollFromOutboundQueue();
                    writeToSelectedConnections();
                } catch (RuntimeException e) {
                    logger.error("unexpected error encountered", e);
                }

                try {
                    // prevents endless loop wasting cpu time
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    isRunning = false;
                    return;
                }
            }
        }

        private void pollFromInboundQueue() {
            List<Connection> connections = pollConnectionsFromQueue(inboundQueues);
            registerConnectionsToSelector(connections, readSelector, SelectionKey.OP_READ);
        }

        private void pollFromOutboundQueue() {
            List<Connection> connections = pollConnectionsFromQueue(outboundQueues);
            registerConnectionsToSelector(connections, writeSelector, SelectionKey.OP_WRITE);
        }

        private List<Connection> pollConnectionsFromQueue(List<Queue<Connection>> queues) {
            final Queue<Connection> theQueue = queues.get(index);

            List<Connection> result = new ArrayList<>(MAX_POLL_SIZE);

            int pollCount = 0;
            while (pollCount < MAX_POLL_SIZE) {
                final Connection conn = theQueue.poll();
                if (conn != null) {
                    pollCount += 1;
                    result.add(conn);
                } else {
                    break;  // fail-fast
                }
            }

            return result;
        }

        private void registerConnectionsToSelector(List<Connection> connections, Selector dstSelector, int operation) {
            for (Connection conn : connections) {
                SocketChannel socketChannel = conn.socketChannel();
                try {
                    socketChannel.register(dstSelector, operation, conn);
                } catch (ClosedChannelException e) {
                    logger.warn("channel already closed when starting processing", e);
                }
            }
        }

        private void readFromSelectedConnections() {
            doSelectOnSelector(readSelector, this::readBySelectionKey);
        }

        private void writeToSelectedConnections() {
            doSelectOnSelector(writeSelector, this::writeBySelectionKey);
        }

        private void doSelectOnSelector(Selector selector, Consumer<SelectionKey> consumer) {
            final int nSelected;
            try {
                nSelected = selector.selectNow();
            } catch (IOException e) {
                logger.error("error encountered when selecting connections", e);
                return;
            }

            if (nSelected > 0) {
                final Set<SelectionKey> keys = selector.selectedKeys();
                final Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    final SelectionKey key = it.next();
                    consumer.accept(key);
                    it.remove();
                }
            }
        }

        private void readBySelectionKey(SelectionKey key) {
            final var conn = (Connection) key.attachment();
            final ConnectionReader reader = getReader(conn);

            boolean needCancel = false;
            try {
                final int nBytesRead = reader.read(conn);
                if (nBytesRead > 0) {
                    if (reader.hasNext()) {
                        final Object protocolObject = reader.next();
                        final Future<Object> future = submitProtocolObjectProcessTask(protocolObject);
                        submittedTasks.add(new Pair<>(conn, future));

                        logger.debug("processing submitted: Connection#{}", conn.id());
                    }
                }
            } catch (IOException e) {
                logger.warn("error encountered when reading from connection: Connection#" + conn.id(), e);
                needCancel = true;
            } catch (EndOfStreamException e) {
                logger.info("connection has been closed: Connection#{}", conn.id());
                needCancel = true;
            }

            if (needCancel) {
                key.cancel();
                close(conn);
            }
        }

        private void writeBySelectionKey(SelectionKey key) {
            final var conn = (Connection) key.attachment();
            final ConnectionWriter writer = getWriter(conn);

            boolean needCancel = false;
            try {
                final int nBytesWrite = writer.write(conn);
                if (nBytesWrite == 0) {
                    // no need to close connection here, lest later requests
                    // should create an new connection
                    // we just want to pause the selection, and we are not
                    // going to call SelectionKey#cancel(), because it would
                    // make the connection closed
                    key.interestOps(0);

                    logger.debug("write selection on connection paused: Connection#{}", conn.id());
                }
            } catch (IOException | RuntimeException e) {
                logger.error("error encountered when writing to connection: Connection#" + conn.id(), e);
                needCancel = true;
            }

            if (needCancel) {
                key.cancel();
                close(conn);
            }
        }

        private void checkSubmittedTasks() {
            final Iterator<Pair<Connection, Future<Object>>> it = submittedTasks.iterator();
            while (it.hasNext()) {
                final Pair<Connection, Future<Object>> pair = it.next();
                final var conn = pair.getFirst();
                final var future = pair.getSecond();

                if (future.isDone()) {
                    try {
                        final Object returnObject = future.get();
                        final ConnectionWriter writer = getWriter(conn);

                        writer.add(returnObject);
                        outboundQueues.get(getIOThreadIndex(conn)).add(conn);
                        logger.debug("processing finished: Connection#{}", conn.id());
                    } catch (InterruptedException e) {
                        isRunning = false;
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        final Throwable cause = e.getCause();
                        logger.warn("error encountered when processing message from connection: Connection#" +
                                            conn.id(), cause);
                    } finally {
                        it.remove();
                    }
                }
            }
        }

        private void close(Connection conn) {
            readers.remove(conn.id());
            writers.remove(conn.id());

            try {
                conn.close();
            } catch (IOException ioe) {
                logger.warn("error encountered when closing connection: Connection#" + conn.id(), ioe);
            }
        }

        private ConnectionReader getReader(Connection conn) {
            return readers.computeIfAbsent(conn.id(), k -> protocolFactory.newConnectionReader());
        }

        private ConnectionWriter getWriter(Connection conn) {
            return writers.computeIfAbsent(conn.id(), k -> protocolFactory.newConnectionWriter());
        }
    }

    private class ProtocolObjectProcessTask implements Callable<Object> {
        private final Object protocolObject;

        ProtocolObjectProcessTask(Object protocolObject) {
            this.protocolObject = protocolObject;
        }

        @Override
        public Object call() {
            return protocolObjectProcessor.process(protocolObject);
        }
    }

    private class WorkerThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber;
        private final ThreadGroup group;
        private final String namePrefix;

        WorkerThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            this.threadNumber = new AtomicInteger(0);
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = config.getServerNameWithoutVersion() + "-Worker-";
        }

        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.incrementAndGet());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
