package codesquad.http;

import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String version;
    private final String host;
    private final String target;
    private final Map<String, String> query;
    private String body;
    private final Map<String, String> headers;

    public HttpRequest(String method, String version, String host, String path, Map<String, String> query, String body, Map<String, String> headers) {
        this.method = method;
        this.version = version;
        this.host = host;
        this.target = path;
        this.query = query;
        this.body = body;
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getRequestLine() {
        return method + " " + target + " " + version;
    }

    public String getQuery(String key) {
        return query.get(key);
    }
}
