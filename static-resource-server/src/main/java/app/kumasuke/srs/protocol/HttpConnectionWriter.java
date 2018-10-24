package app.kumasuke.srs.protocol;

import app.kumasuke.srs.AbstractConnectionWriter;
import app.kumasuke.srs.Connection;
import app.kumasuke.srs.util.DynamicByteBuffer;

import javax.annotation.Nonnull;
import java.io.IOException;

public class HttpConnectionWriter extends AbstractConnectionWriter<HttpResponse> {
    private final DynamicByteBuffer writeBuffer = new DynamicByteBuffer();

    @Override
    public int write(@Nonnull Connection connection) throws IOException {
        HttpResponse response;
        while ((response = next()) != null) {
            byte[] bytes = HttpSupport.toBytes(response);
            // same Connection won't send a second request before the first
            // completes. When this happens, it can only because the client
            // cancels receiving and re-send another request
            // under such circumstances, we have to cancel data sending of
            // the first request
            if (!writeBuffer.isEmpty()) {
                writeBuffer.clear();
            }
            writeBuffer.append(bytes);

            logger.info("Connection#{} response enqueued: statusCode = {}, statusText = {}",
                         connection.id(), response.status().getStatusCode(), response.status().getReasonPhrase());
        }

        if (writeBuffer.isEmpty()) {
            return 0;
        } else {
            int nBytesWrite = write(writeBuffer, connection);
            logger.debug("Connection#{} bytes written: {} bytes", connection.id(), nBytesWrite);

            return nBytesWrite;
        }
    }

    @Override
    public void add(@Nonnull Object object) {
        if (object instanceof HttpResponse) {
            final var response = (HttpResponse) object;
            objects.add(response);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
