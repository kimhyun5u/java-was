package codesquad.http;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class HttpRequest {
    private final String method;
    private final String version;
    private final String path;
    private final Map<String, String> query;
    private final String body;
    private final Map<String, String> headers;

    public HttpRequest(String method, String version, String body, String path, Map<String, String> query, Map<String, String> headers) {
        this.method = method;
        this.version = version;
        this.body = body;
        this.path = path;
        this.query = query;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public String getRequestLine() {
        return method + " " + path + " " + version;
    }

    public String getQuery(String key) {
        return query.get(key);
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public static HttpRequest from(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        List<String> headerLines = new ArrayList<>();
        StringBuilder bodyBuilder = new StringBuilder();
        String line;

        // 헤더 읽기
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }

        // 본문 읽기
        int contentLength = 0;
        for (String headerLine : headerLines) {
            if (headerLine.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                break;
            }
        }

        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars);
            bodyBuilder.append(bodyChars);
        }

        String body = bodyBuilder.toString();

        // 요청 라인 파싱
        String[] requestLine = headerLines.get(0).split(" ");
        String method = requestLine[0];
        String target;
        Map<String, String> query = new HashMap<>();

        // URL 디코딩 및 쿼리 파싱
        String decodedUrl = URLDecoder.decode(requestLine[1], "UTF-8");
        if (decodedUrl.contains("?")) {
            String[] urlParts = decodedUrl.split("\\?", 2);
            target = urlParts[0];
            parseQueryString(urlParts[1], query);
        } else {
            target = decodedUrl;
        }

        // 헤더 파싱
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headerLines.size(); i++) {
            String[] header = headerLines.get(i).split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }

        // POST 요청 처리
        if ("POST".equalsIgnoreCase(method) && !body.isEmpty()) {
            String contentType = headers.getOrDefault("Content-Type", "").toLowerCase();
            if (contentType.contains("application/x-www-form-urlencoded")) {
                parseQueryString(body, query);
            }
            // 다른 Content-Type (예: application/json)에 대한 처리는 여기에 추가할 수 있습니다.
        }

        return new HttpRequest(method, requestLine[2], body, target, query, headers);
    }

    private static void parseQueryString(String queryString, Map<String, String> query) throws UnsupportedEncodingException {
        for (String param : queryString.split("&")) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length == 2) {
                query.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
            } else if (keyValue.length == 1) {
                query.put(keyValue[0], "");
            }
        }
    }
}
