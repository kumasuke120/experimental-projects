package app.kumasuke.srs.protocol;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
class HttpRequest {
    private final String version;
    private final HttpMethod method;
    private final String requestUri;
    private final HttpHeaders headers;
    private final byte[] body;

    HttpRequest(@Nonnull HttpMethod method,
                @Nonnull String requestUri,
                @Nonnull String version,
                @Nonnull HttpHeaders headers,
                @Nonnull byte[] body) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    @Nonnull
    HttpMethod method() {
        return method;
    }

    @Nonnull
    String requestUri() {
        return requestUri;
    }

    @Nonnull
    String version() {
        return version;
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
