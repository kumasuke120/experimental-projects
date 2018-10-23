package app.kumasuke.srs.protocol;

import app.kumasuke.srs.AbstractConnectionReader;
import app.kumasuke.srs.Connection;
import app.kumasuke.srs.util.DynamicByteBuffer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.NoSuchElementException;

import static app.kumasuke.srs.protocol.HttpSupport.POSITION_NOT_FOUND;

public class HttpConnectionReader extends AbstractConnectionReader<HttpRequest> {
    private final DynamicByteBuffer readBuffer = new DynamicByteBuffer();

    @Override
    public int read(@Nonnull Connection connection) throws IOException {
        int nBytesRead = read(connection, readBuffer);
        if (nBytesRead > 0) {
            logger.debug("Connection#{} bytes read: {} bytes", connection.id(), nBytesRead);

            final int endOfRequest = HttpSupport.findNextEndOfRequest(readBuffer);
            if (endOfRequest != POSITION_NOT_FOUND) {
                final byte[] tmp = readBuffer.pop(endOfRequest);
                final var httpRequest = HttpSupport.parseRequest(new DynamicByteBuffer(tmp));

                logger.info("Connection#{} request enqueued: method = {}, requestUri = {}",
                             connection.id(), httpRequest.method(), httpRequest.requestUri());
                objects.add(httpRequest);
            }
        }

        return nBytesRead;
    }

    @Nonnull
    @Override
    public HttpRequest next() {
        final HttpRequest first = super.next();
        if (first != null) {
            return first;
        } else {
            throw new NoSuchElementException();
        }
    }
}
