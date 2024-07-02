package codesquad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String version;
    private final String host;
    private final String path;
    private final String query;
    private final String body;
    private final Map<String, String> headers;

    private HttpRequest(String method, String version, String host, String path, String query, String body, Map<String, String> headers) {
        this.method = method;
        this.version = version;
        this.host = host;
        this.path = path;
        this.query = query;
        this.body = body;
        this.headers = headers;
    }

    public static HttpRequest of(final List<String> lines) {
        // Request Line
        String[] requestLine = lines.get(0).split(" ");
        int headerCounter = 1;

        Map<String, String> headers = new HashMap<>();

        while (headerCounter < lines.size() && !lines.get(headerCounter).isEmpty()) {
            String[] header = lines.get(headerCounter++).split(":");
            headers.put(header[0].trim(), header[1].trim());
        }
        StringBuilder sb = new StringBuilder();

        for (int bodyCounter = headerCounter; bodyCounter < lines.size(); bodyCounter++) {
            sb.append(lines.get(bodyCounter));
            sb.append("\n");
        }


        return new HttpRequest(requestLine[0], requestLine[2], "", requestLine[1], "", sb.toString(), headers);
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
