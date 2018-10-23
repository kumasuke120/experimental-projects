package app.kumasuke.srs;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class Connection implements Closeable {
    private final long id;
    private final SocketChannel socketChannel;

    Connection(long id,
               @Nonnull SocketChannel socketChannel) {
        this.id = id;
        this.socketChannel = socketChannel;
    }

    public long id() {
        return id;
    }

    @Nonnull
    SocketChannel socketChannel() {
        return socketChannel;
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return id == that.id &&
                Objects.equals(socketChannel, that.socketChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, socketChannel);
    }
}
