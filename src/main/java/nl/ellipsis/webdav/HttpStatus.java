package nl.ellipsis.webdav;

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus {
    /**
     * 200 OK, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1
     * documentation</a>.
     */
    OK(200, "OK"),
    /**
     * 201 Created, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1
     * documentation</a>.
     */
    CREATED(201, "Created"),
    /**
     * 202 Accepted, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1
     * documentation</a>.
     */
    ACCEPTED(202, "Accepted"),
    /**
     * 204 No Content, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1
     * documentation</a>.
     */
    NO_CONTENT(204, "No Content"),
    /**
     * 205 Reset Content, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.6">HTTP/1.1
     * documentation</a>.
     */
    RESET_CONTENT(205, "Reset Content"),
    /**
     * 206 Reset Content, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7">HTTP/1.1
     * documentation</a>.
     */
    PARTIAL_CONTENT(206, "Partial Content"),
    /**
     * 207 Multi-Status, see
     * <a href="https://datatracker.ietf.org/doc/html/rfc4918#section-11.1">
     * HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)
     * documentation</a>.
     */
    MULTI_STATUS(207, "Multi-Status"),
    /**
     * 301 Moved Permanently, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1
     * documentation</a>.
     */
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    /**
     * 302 Found, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.3">HTTP/1.1
     * documentation</a>.
     */
    FOUND(302, "Found"),
    /**
     * 303 See Other, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1
     * documentation</a>.
     */
    SEE_OTHER(303, "See Other"),
    /**
     * 304 Not Modified, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1
     * documentation</a>.
     */
    NOT_MODIFIED(304, "Not Modified"),
    /**
     * 305 Use Proxy, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.6">HTTP/1.1
     * documentation</a>.
     */
    USE_PROXY(305, "Use Proxy"),
    /**
     * 307 Temporary Redirect, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1
     * documentation</a>.
     */
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    /**
     * 400 Bad Request, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1
     * documentation</a>.
     */
    BAD_REQUEST(400, "Bad Request"),
    /**
     * 401 Unauthorized, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1
     * documentation</a>.
     */
    UNAUTHORIZED(401, "Unauthorized"),
    /**
     * 402 Payment Required, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.3">HTTP/1.1
     * documentation</a>.
     */
    PAYMENT_REQUIRED(402, "Payment Required"),
    /**
     * 403 Forbidden, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1
     * documentation</a>.
     */
    FORBIDDEN(403, "Forbidden"),
    /**
     * 404 Not Found, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1
     * documentation</a>.
     */
    NOT_FOUND(404, "Not Found"),
    /**
     * 405 Method Not Allowed, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6">HTTP/1.1
     * documentation</a>.
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    /**
     * 406 Not Acceptable, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1
     * documentation</a>.
     */
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    /**
     * 407 Proxy Authentication Required, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.8">HTTP/1.1
     * documentation</a>.
     */
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    /**
     * 408 Request Timeout, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.9">HTTP/1.1
     * documentation</a>.
     */
    REQUEST_TIMEOUT(408, "Request Timeout"),
    /**
     * 409 Conflict, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1
     * documentation</a>.
     */
    CONFLICT(409, "Conflict"),
    /**
     * 410 Gone, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1
     * documentation</a>.
     */
    GONE(410, "Gone"),
    /**
     * 411 Length Required, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.12">HTTP/1.1
     * documentation</a>.
     */
    LENGTH_REQUIRED(411, "Length Required"),
    /**
     * 412 Precondition Failed, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1
     * documentation</a>.
     */
    PRECONDITION_FAILED(412, "Precondition Failed"),
    /**
     * 413 Request Entity Too Large, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.14">HTTP/1.1
     * documentation</a>.
     */
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    /**
     * 414 Request-URI Too Long, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.15">HTTP/1.1
     * documentation</a>.
     */
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    /**
     * 415 Unsupported Media Type, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1
     * documentation</a>.
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    /**
     * 416 Requested Range Not Satisfiable, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.17">HTTP/1.1
     * documentation</a>.
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    /**
     * 417 Expectation Failed, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.18">HTTP/1.1
     * documentation</a>.
     */
    EXPECTATION_FAILED(417, "Expectation Failed"),
    /**
     * 422 Unprocessable Entity, see
     * <a href="https://datatracker.ietf.org/doc/html/rfc4918#section-11.1">
     * HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)
     * documentation</a>.
     */
    UNPROCESSABLE_ENITY(422, "Unprocessable Entity"),
    /**
     * 423 Locked, see
     * <a href="https://datatracker.ietf.org/doc/html/rfc4918#section-11.1">
     * HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)
     * documentation</a>.
     */
    LOCKED(423, "Locked"),
    /**
     * 424 Failed Dependency, see
     * <a href="https://datatracker.ietf.org/doc/html/rfc4918#section-11.1">
     * HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)
     * documentation</a>.
     */
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    /**
     * 428 Precondition required, see
     * <a href="https://tools.ietf.org/html/rfc6585#section-3">RFC 6585:
     * Additional HTTP Status Codes</a>.
     */
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    /**
     * 429 Too Many Requests, see
     * <a href="https://tools.ietf.org/html/rfc6585#section-4">RFC 6585:
     * Additional HTTP Status Codes</a>.
     */
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    /**
     * 431 Request Header Fields Too Large, see
     * <a href="https://tools.ietf.org/html/rfc6585#section-5">RFC 6585:
     * Additional HTTP Status Codes</a>.
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    /**
     * 500 Internal Server Error, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1
     * documentation</a>.
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    /**
     * 501 Not Implemented, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.2">HTTP/1.1
     * documentation</a>.
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),
    /**
     * 502 Bad Gateway, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.3">HTTP/1.1
     * documentation</a>.
     */
    BAD_GATEWAY(502, "Bad Gateway"),
    /**
     * 503 Service Unavailable, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1
     * documentation</a>.
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    /**
     * 504 Gateway Timeout, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5">HTTP/1.1
     * documentation</a>.
     */
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    /**
     * 505 HTTP Version Not Supported, see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.6">HTTP/1.1
     * documentation</a>.
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    /**
     * 507 Insufficient Storage, see
     * <a href="https://datatracker.ietf.org/doc/html/rfc4918#section-11.1">
     * HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)
     * documentation</a>.
     */
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    /**
     * 511 Network Authentication Required, see
     * <a href="https://tools.ietf.org/html/rfc6585#section-6">RFC 6585:
     * Additional HTTP Status Codes</a>.
     */
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
    ;

    private static Map<Integer,HttpStatus> codeReverseMap;

    private final int value;
    private final String reasonPhrase;

    private HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public static HttpStatus valueOf(int code) {
        if(codeReverseMap == null) {
            Map<Integer,HttpStatus> codeReverseMapBuilder = new HashMap<>();
            for(HttpStatus hs: HttpStatus.values()) {
                codeReverseMapBuilder.put(hs.value(), hs);
            }
            codeReverseMap = codeReverseMapBuilder;
        }
        return codeReverseMap.get(code);
    }
}
