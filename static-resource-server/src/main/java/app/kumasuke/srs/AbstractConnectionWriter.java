package app.kumasuke.srs;

import app.kumasuke.srs.util.DynamicByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public abstract class AbstractConnectionWriter<T> implements ConnectionWriter {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionWriter.class);

    private static final int BUFFER_SIZE = 8192;

    protected final Queue<T> objects = new LinkedList<>();

    protected final int write(@Nonnull DynamicByteBuffer srcBuffer, @Nonnull Connection connection)
            throws IOException {
        final var buffer = ByteBuffer.allocate(BUFFER_SIZE);

        final int popLength = BUFFER_SIZE > srcBuffer.length() ? srcBuffer.length() : BUFFER_SIZE;
        if (popLength != 0) {
            final byte[] bytes = srcBuffer.pop(popLength);
            buffer.put(bytes);
            buffer.flip();
        }

        int totalBytesWrite = 0;
        int nBytesWrite;
        do {
            nBytesWrite = connection.socketChannel().write(buffer);
            totalBytesWrite += nBytesWrite;
        } while (nBytesWrite > 0 && buffer.hasRemaining());

        return totalBytesWrite;
    }

    @Nullable
    protected final T next() {
        try {
            return objects.poll();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
