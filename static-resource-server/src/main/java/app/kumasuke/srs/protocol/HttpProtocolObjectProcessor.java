package app.kumasuke.srs.protocol;

import app.kumasuke.srs.AbstractProtocolObjectProcessor;
import app.kumasuke.srs.util.Config;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static app.kumasuke.srs.protocol.HttpSupport.*;

public class HttpProtocolObjectProcessor extends AbstractProtocolObjectProcessor {
    private static final String ALLOWED_HTTP_METHODS = "OPTIONS, GET, HEAD";
    private static final Set<String> DEFAULT_WELCOME_FILES = Set.of("index.html", "index.htm");
    private static final Set<String> TEXT_FILE_MIMETYPES =
            Set.of("text/css", "text/html", "text/javascript", "application/javascript", "text/plain");
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    HttpProtocolObjectProcessor(@Nonnull Config config) {
        super(config);
    }

    @Nonnull
    @Override
    public Object process(@Nonnull Object object) {
        if (object instanceof HttpRequest) {
            final var request = (HttpRequest) object;

            final String version = request.version();
            HttpStatus status;
            final var headers = new HttpHeaders();
            byte[] body;

            final HttpMethod method = request.method();
            switch (request.method()) {
                case GET:
                case HEAD: {
                    final Path filePath;
                    try {
                        filePath = getLocalFilePath(request.requestUri());
                    } catch (InvalidPathException e) {
                        status = HttpStatus.BAD_REQUEST;
                        body = EMPTY_BYTE_ARRAY;
                        break;
                    }

                    if (filePath == null) {
                        status = HttpStatus.NOT_FOUND;
                        body = EMPTY_BYTE_ARRAY;
                    } else {
                        final boolean needBody = (method == HttpMethod.GET);
                        try {
                            status = HttpStatus.OK;
                            body = needBody ? Files.readAllBytes(filePath): EMPTY_BYTE_ARRAY;
                            putGETHeaders(headers, filePath, needBody);
                        } catch (IOException e) {
                            logFileProcessIOException(e, filePath);
                            body = EMPTY_BYTE_ARRAY;
                            status = HttpStatus.INTERNAL_SERVER_ERROR;
                        }
                    }

                    break;
                }
                case OPTIONS: {
                    status = HttpStatus.OK;
                    body = EMPTY_BYTE_ARRAY;
                    headers.put(HEADER_ALLOW, ALLOWED_HTTP_METHODS.getBytes());
                    break;
                }
                default: {
                    status = HttpStatus.METHOD_NOT_ALLOWED;
                    body = EMPTY_BYTE_ARRAY;
                    break;
                }
            }
            putCommonHeaders(headers);

            return new HttpResponse(version, status, headers, body);
        } else {
            throw new AssertionError();
        }
    }

    private void logFileProcessIOException(IOException e, Path filePath) {
        logger.error("error encountered when reading local file: " + filePath.toString(), e);
    }

    private void putGETHeaders(HttpHeaders headers, Path filePath, boolean withContentLength) throws IOException {
        if (withContentLength) {
            final String contentLength = Long.toString(Files.size(filePath));
            headers.put(HEADER_CONTENT_LENGTH, contentLength.getBytes());
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType != null) {
            if (TEXT_FILE_MIMETYPES.contains(contentType)) {
                contentType += "; charset=" + config.getServerDefaultCharset();
            }
            headers.put(HEADER_CONTENT_TYPE, contentType.getBytes());
        }
    }

    private void putCommonHeaders(HttpHeaders headers) {
        final var serverDate = getServerDateString();
        headers.put(HEADER_DATE, serverDate.getBytes());

        final var serverName = config.getServerName();
        headers.put(HEADER_SERVER, serverName.getBytes());

        if (!headers.containsName(HEADER_CONTENT_LENGTH)) {
            headers.put(HEADER_CONTENT_LENGTH, "0".getBytes());
        }
    }

    private String getServerDateString() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    private Path getLocalFilePath(String requestUri) {
        final String relativePath = requestUri.charAt(0) == '/' ? requestUri.substring(1) : requestUri;
        final Path path = config.getServerRootDirectory().resolve(relativePath);

        if (Files.isDirectory(path)) {
            for (String fileName : DEFAULT_WELCOME_FILES) {
                final Path newPath = path.resolve(fileName);
                if (Files.exists(newPath)) {
                    return newPath;
                }
            }

            return null;
        } else {
            return Files.exists(path) ? path : null;
        }
    }
}
