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

public abstract class AbstractConnectionReader<T> implements ConnectionReader {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionReader.class);

    private static final int END_OF_STREAM = -1;
    private static final int BUFFER_SIZE = 2048;

    protected final Queue<T> objects = new LinkedList<>();

    @Override
    public boolean hasNext() {
        return !objects.isEmpty();
    }

    @Nullable
    @Override
    public T next() {
        try {
            return objects.poll();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    protected final int read(@Nonnull Connection connection, @Nonnull DynamicByteBuffer dstBuffer)
            throws IOException {
        final var buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int totalBytesRead = 0;
        int nBytesRead;
        while ((nBytesRead = connection.socketChannel().read(buffer)) > 0) {
            buffer.flip();

            final int nRemainingBytes = buffer.remaining();
            final byte[] tmp = new byte[nRemainingBytes];
            buffer.get(tmp);
            dstBuffer.append(tmp);

            buffer.compact();
            totalBytesRead += nBytesRead;
        }

        if (nBytesRead == END_OF_STREAM) {
            throw new EndOfStreamException();
        }

        return totalBytesRead;
    }
}
