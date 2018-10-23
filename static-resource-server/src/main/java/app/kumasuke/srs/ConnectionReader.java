package app.kumasuke.srs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public interface ConnectionReader {
    int read(@Nonnull Connection connection) throws IOException;

    boolean hasNext();

    @Nullable
    Object next();
}
