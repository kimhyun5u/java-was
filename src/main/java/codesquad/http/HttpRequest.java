package codesquad.http;

import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String version;
    private final String host;
    private final String path;
    private final Map<String, String> query;
    private final String body;
    private final Map<String, String> headers;

    public HttpRequest(String method, String version, String host, String path, Map<String, String> query, String body, Map<String, String> headers) {
        this.method = method;
        this.version = version;
        this.host = host;
        this.path = path;
        this.query = query;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }


    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getRequestLine() {
        return method + " " + path + " " + version;
    }
}
