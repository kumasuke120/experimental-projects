package app.kumasuke.srs.protocol;

import app.kumasuke.srs.util.Config;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

class HttpStaticResourceProcessor extends ChannelInboundHandlerAdapter {
    private static final String ALLOWED_HTTP_METHODS = "OPTIONS, GET, HEAD";
    private static final Set<String> DEFAULT_WELCOME_FILES = Set.of("index.html", "index.htm");
    private static final Set<String> TEXT_FILE_MIMETYPES =
            Set.of("text/css", "text/html", "text/javascript", "application/javascript", "text/plain");
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private final Config config;

    HttpStaticResourceProcessor(Config config) {
        this.config = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            final FullHttpRequest request = (FullHttpRequest) msg;
            final HttpMethod method = request.method();
            final String requestUri = request.uri();
            final HttpVersion version = request.protocolVersion();

            FullHttpResponse response;
            try {
                response = generateResponse(method, requestUri, version);
            } catch (InvalidPathException | IOException e) {
                throw new ObjectCarriedException(e, version);
            }

            ctx.writeAndFlush(response);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private FullHttpResponse generateResponse(HttpMethod method, String requestUri, HttpVersion version)
            throws IOException {
        FullHttpResponse response;
        if (HttpMethod.GET.equals(method) ||
                HttpMethod.HEAD.equals(method)) {
            final Path filePath = getLocalFilePath(requestUri);
            if (filePath == null) {
                response = new DefaultFullHttpResponse(
                        version,
                        HttpResponseStatus.NOT_FOUND,
                        Unpooled.copiedBuffer(EMPTY_BYTE_ARRAY)
                );
            } else {
                final boolean needBody = HttpMethod.GET.equals(method);
                final var body = needBody ? Files.readAllBytes(filePath) : EMPTY_BYTE_ARRAY;

                response = new DefaultFullHttpResponse(
                        version,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(body)
                );
                putGETHeaders(response, filePath, needBody);
            }
        } else if (HttpMethod.OPTIONS.equals(method)) {
            response = new DefaultFullHttpResponse(
                    version,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(EMPTY_BYTE_ARRAY)
            );
            putOPTIONSHeader(response);
        } else {
            response = new DefaultFullHttpResponse(
                    version,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(EMPTY_BYTE_ARRAY)
            );
        }
        putCommonHeaders(response);

        return response;
    }

    private void putGETHeaders(FullHttpResponse response,
                               Path filePath, boolean withContentLength) throws IOException {
        if (withContentLength) {
            final String contentLength = Long.toString(Files.size(filePath));
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType != null) {
            if (TEXT_FILE_MIMETYPES.contains(contentType)) {
                contentType += "; charset=" + config.getServerDefaultCharset();
            }
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType);
        }
    }

    private void putOPTIONSHeader(FullHttpResponse response) {
        response.headers().add(HttpHeaderNames.ALLOW, ALLOWED_HTTP_METHODS);
    }

    private void putCommonHeaders(FullHttpResponse response) {
        final HttpHeaders headers = response.headers();

        headers.add(HttpHeaderNames.DATE, new Date());

        final var serverName = config.getServerName();
        headers.add(HttpHeaderNames.SERVER, serverName);

        if (!headers.contains(HttpHeaderNames.CONTENT_LENGTH)) {
            headers.add(HttpHeaderNames.CONTENT_LENGTH, "0");
        }
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ObjectCarriedException) {
            final Object value = ((ObjectCarriedException) cause).getValue();
            final Throwable causeOfCause = cause.getCause();

            FullHttpResponse response = null;
            if (causeOfCause instanceof InvalidPathException) {
                final var version = (HttpVersion) value;
                response = new DefaultFullHttpResponse(
                        version,
                        HttpResponseStatus.BAD_REQUEST,
                        Unpooled.copiedBuffer(EMPTY_BYTE_ARRAY)
                );
            } else if (causeOfCause instanceof IOException) {
                final var version = (HttpVersion) value;
                response = new DefaultFullHttpResponse(
                        version,
                        HttpResponseStatus.INTERNAL_SERVER_ERROR,
                        Unpooled.copiedBuffer(EMPTY_BYTE_ARRAY)
                );
            }

            if (response != null) {
                ctx.writeAndFlush(response);
            } else {
                super.exceptionCaught(ctx, cause);
            }
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    private static class ObjectCarriedException extends RuntimeException {
        private final Object value;

        ObjectCarriedException(Throwable cause, Object value) {
            super(cause);
            this.value = value;
        }

        Object getValue() {
            return value;
        }
    }
}
