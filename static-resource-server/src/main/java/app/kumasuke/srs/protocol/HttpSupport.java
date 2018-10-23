package app.kumasuke.srs.protocol;

import app.kumasuke.srs.IllegalMessageException;
import app.kumasuke.srs.util.DynamicByteBuffer;

import javax.annotation.Nonnull;

class HttpSupport {
    static final int POSITION_NOT_FOUND = -1;

    static final String HEADER_CONTENT_LENGTH = "Content-Length";
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    static final String HEADER_SERVER = "Sever";
    static final String HEADER_DATE = "Date";
    static final String HEADER_ALLOW = "Allow";

    private static final String HEADER_HOST = "Host";

    private static final byte[] BYTES_SPACE = " ".getBytes();
    private static final byte[] BYTES_CRLF = "\r\n".getBytes();
    private static final byte[] BYTES_COLON = ":".getBytes();

    @Nonnull
    static byte[] toBytes(@Nonnull HttpResponse response) {
        /*
         * Http Response Structure:
         * Response = Status-Line
         *            *(( general-header
         *              | response-header
         *              | entity-header ) CRLF)  ; message-headers
         *             CRLF
         *             [ message-body ]
         * See Also: https://tools.ietf.org/html/rfc2616
         */

        final var buffer = new DynamicByteBuffer();

        // Status-Line
        appendStatusLine(buffer, response);

        // message-headers
        appendHeaders(buffer, response);
        buffer.append(BYTES_CRLF);

        // message-body
        buffer.append(response.body());

        return buffer.toByteArray();
    }

    private static void appendStatusLine(DynamicByteBuffer dst, HttpResponse response) {
        /*
         * Status-Line:
         *  Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
         */
        dst.append(("HTTP/" + response.version()).getBytes());
        dst.append(BYTES_SPACE);
        final var status = response.status();
        final var statusCodeStr = Integer.toString(status.getStatusCode());
        dst.append(statusCodeStr.getBytes());
        dst.append(BYTES_SPACE);
        dst.append(status.getReasonPhrase().getBytes());
        dst.append(BYTES_CRLF);
    }

    private static void appendHeaders(DynamicByteBuffer dst, HttpResponse response) {
        final var headers = response.headers();
        for (String fieldName : headers.getAllNames()) {
            for (byte[] value : headers.getValues(fieldName)) {
                dst.append(fieldName.getBytes());
                dst.append(BYTES_COLON);
                dst.append(BYTES_SPACE);
                dst.append(value);
                dst.append(BYTES_CRLF);
            }
        }
    }

    @Nonnull
    static HttpRequest parseRequest(@Nonnull DynamicByteBuffer src) {
        /*
         * Http Request Structure:
         *  Request = Request-Line
         *            *(( general-header
         *             | request-header
         *             | entity-header ) CRLF)  ; message-headers
         *            CRLF
         *            [ message-body ]
         * See Also: https://tools.ietf.org/html/rfc2616
         */

        /*
         * Request-Line:
         *  Request-Line = Method SP Request-URI SP HTTP-Version CRLF
         */
        final int endOfRequestLine = findNextEndOfLine(src, 0);
        if (endOfRequestLine == POSITION_NOT_FOUND) {
            throw new IllegalMessageException("Cannot parse request as http request");
        }
        final HttpMethod method = parseHttpMethod(src);
        final String requestUri = parseRequestUri(src);
        final String version = parseVersion(src);

        // message-headers
        final int endOfHeaders = findNextEndOfHeaders(src, endOfRequestLine);
        if (endOfHeaders == POSITION_NOT_FOUND) {
            throw new IllegalMessageException("Cannot parse http headers from request");
        }
        final HttpHeaders headers = parseHttpHeaders(src, endOfRequestLine, endOfHeaders);
        final int contentLength = parseContentLength(src, endOfRequestLine, endOfHeaders);

        // message-body
        final byte[] body = contentLength == 0 ? new byte[0] : src.get(endOfHeaders, contentLength);
        return createNewRequest(method, requestUri, version, headers, body);
    }

    private static HttpMethod parseHttpMethod(DynamicByteBuffer src) {
        for (HttpMethod method : HttpMethod.values()) {
            if (headMatches(src, 0, method.name(), true)) {
                return method;
            }
        }

        throw new IllegalMessageException("Cannot parse http method from request");
    }

    private static String parseRequestUri(DynamicByteBuffer src) {
        final var builder = new StringBuilder();

        boolean metFirstSpace = false;
        for (int i = 0; i < src.length(); i++) {
            final byte c = src.get(i);
            if (Character.isSpaceChar(c)) {
                if (metFirstSpace) {
                    break;
                } else {
                    metFirstSpace = true;
                }
            } else if (metFirstSpace) {
                builder.appendCodePoint(c);
            }
        }

        if (builder.length() == 0) {
            throw new IllegalMessageException("Cannot parse request uri from request");
        } else {
            return builder.toString();
        }
    }

    private static String parseVersion(DynamicByteBuffer src) {
        final String anchor = " HTTP/";
        final var builder = new StringBuilder();

        boolean append = false;
        for (int i = 0; i < src.length(); i++) {
            final byte c = src.get(i);

            if (append) {
                if (Character.isDigit(c) || c == '.') {
                    builder.appendCodePoint(c);
                } else {
                    break;
                }
            }

            if (anchor.charAt(0) == c && headMatches(src, i, anchor, true)) {
                i += anchor.length() - 1;
                append = true;
            }
        }

        if (builder.length() == 0) {
            throw new IllegalMessageException("Cannot parse http version from request");
        } else {
            return builder.toString();
        }
    }

    private static HttpHeaders parseHttpHeaders(DynamicByteBuffer src, int startIndex, int endIndex) {
        /*
         * message-header = field-name ":" [ field-value ]
         * field-name     = token
         * field-value    = *( field-content | LWS )
         * field-content  = <the OCTETs making up the field-value
         *                  and consisting of either *TEXT or combinations
         *                  of token, separators, and quoted-string>
         */

        final StringBuilder fieldNameBuilder = new StringBuilder();

        final HttpHeaders result = new HttpHeaders();

        for (int i = startIndex; i < endIndex; ) {
            final int nextEndOfLine = findNextEndOfLine(src, i);

            for (int j = i; j < nextEndOfLine; j++) {
                final byte c = src.get(j);
                if (c == ':') {
                    final String fieldName = fieldNameBuilder.toString();
                    final int valueLength = nextEndOfLine - j - 1;
                    final byte[] fieldValue = src.get(j + 1, valueLength);
                    result.put(fieldName, fieldValue);
                } else {
                    fieldNameBuilder.appendCodePoint(c);
                }
            }

            fieldNameBuilder.setLength(0);
            i = nextEndOfLine;
        }

        return result;
    }

    private static HttpRequest createNewRequest(HttpMethod method,
                                                String requestUri,
                                                String version,
                                                HttpHeaders headers,
                                                byte[] body) {
        // converts the absolute url to a relative url with an additional Host header
        if (requestUri.matches("^.+?://[^/]+/.*$")) {
            final String mayBeHost = requestUri.replaceAll("^.+?://([^/]+)/.*$", "$1");
            requestUri = requestUri.replaceAll("^.+?://[^/]+(/.*)$", "$1");
            if (!headers.containsName(HEADER_HOST)) {
                headers.put(HEADER_HOST, mayBeHost.getBytes());
            }
        }

        return new HttpRequest(method, requestUri, version, headers, body);
    }

    static int findNextEndOfRequest(@Nonnull DynamicByteBuffer src) {
        /*
         * Http Request Structure:
         *  Request = Request-Line
         *            *(( general-header
         *             | request-header
         *             | entity-header ) CRLF)  ; message-headers
         *            CRLF
         *            [ message-body ]
         * See Also: https://tools.ietf.org/html/rfc2616
         */

        // Request-Line
        final int endOfRequestLine = findNextEndOfLine(src, 0);
        if (endOfRequestLine == POSITION_NOT_FOUND) return POSITION_NOT_FOUND;

        // message-headers
        final int endOfHeaders = findNextEndOfHeaders(src, endOfRequestLine);
        if (endOfHeaders == POSITION_NOT_FOUND) return POSITION_NOT_FOUND;

        final int contentLength = parseContentLength(src, endOfRequestLine, endOfHeaders);

        // message-body
        if (contentLength == 0) {
            return endOfHeaders;
        } else {
            final int expectedEndOfRequest = endOfHeaders + contentLength;
            if (src.length() >= expectedEndOfRequest) {
                return expectedEndOfRequest;
            } else {
                return POSITION_NOT_FOUND;
            }
        }
    }

    private static int findNextEndOfLine(DynamicByteBuffer src, int startIndex) {
        for (int i = startIndex; i < src.length() - 1; i++) {
            if (src.get(i) == '\r' && src.get(i + 1) == '\n') {
                return i + 2;   // position after the last byte of current line
            }
        }

        return POSITION_NOT_FOUND;
    }

    private static int findNextEndOfHeaders(DynamicByteBuffer src, int startIndex) {
        int prevEndOfLine = findNextEndOfLine(src, startIndex);
        while (prevEndOfLine != POSITION_NOT_FOUND) {
            int nextEndOfLine = findNextEndOfLine(src, prevEndOfLine);
            if (nextEndOfLine != POSITION_NOT_FOUND) {
                if (prevEndOfLine + 2 == nextEndOfLine) {   // current line is empty
                    return nextEndOfLine;
                } else {
                    prevEndOfLine = nextEndOfLine;
                }
            }
        }

        return POSITION_NOT_FOUND;
    }

    private static int parseContentLength(DynamicByteBuffer src, int startIndex, int endIndex) {
        final byte[] fieldValue = findFieldValueByFieldName(src, startIndex, endIndex, HEADER_CONTENT_LENGTH);
        if (fieldValue.length == 0) {
            return 0;
        } else {
            final String fieldValueStr = new String(fieldValue);
            try {
                return Integer.parseInt(fieldValueStr.trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private static byte[] findFieldValueByFieldName(DynamicByteBuffer src, int startIndex, int endIndex,
                                                    @SuppressWarnings("SameParameterValue") String fieldName) {
        for (int i = startIndex; i < endIndex; ) {
            final int nextEndOfLine = findNextEndOfLine(src, i);

            assert nextEndOfLine != POSITION_NOT_FOUND;
            if (headMatches(src, i, fieldName, false)) {
                final int valueStartIndex = i + fieldName.length() + 1;      // adds 1 to skip colon
                final int valueLength = nextEndOfLine - 1 - valueStartIndex;
                return src.get(valueStartIndex, valueLength);
            }

            i = nextEndOfLine;
        }

        return new byte[0];
    }

    private static boolean headMatches(DynamicByteBuffer src, int startIndex, String head,
                                       boolean caseSensitive) {
        final var headBytes = caseSensitive ? head.getBytes() : head.toLowerCase().getBytes();
        for (int i = startIndex; i < src.length() && i - startIndex < headBytes.length; i++) {
            final char srcChar = caseSensitive ? (char) src.get(i) : Character.toLowerCase((char) src.get(i));
            final char fieldChar = (char) headBytes[i - startIndex];

            if (srcChar != fieldChar) {
                return false;
            }
        }

        return true;
    }
}
