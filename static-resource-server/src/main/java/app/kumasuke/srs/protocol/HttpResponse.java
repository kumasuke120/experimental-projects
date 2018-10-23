package app.kumasuke.srs.protocol;

import javax.annotation.Nonnull;

class HttpResponse {
    private final String version;
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final byte[] body;

    HttpResponse(@Nonnull String version,
                 @Nonnull HttpStatus status,
                 @Nonnull HttpHeaders headers,
                 @Nonnull byte[] body) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    @Nonnull
    String version() {
        return version;
    }

    @Nonnull
    HttpStatus status() {
        return status;
    }

    @Nonnull
    HttpHeaders headers() {
        return headers;
    }

    @Nonnull
    byte[] body() {
        return body;
    }
}
