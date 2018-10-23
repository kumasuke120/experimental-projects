package app.kumasuke.srs;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface ConnectionWriter {
    int write(@Nonnull Connection connection) throws IOException;

    void add(@Nonnull Object object);
}
