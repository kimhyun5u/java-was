package codesquad.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {
    private HttpRequestParser() {
    }

    public static HttpRequest parse(final List<String> lines) {
        // Request Line
        String[] requestLine = lines.get(0).split(" ");
        int headerCounter = 1;
        Map<String, String> query = new HashMap<>();
        if (requestLine[1].contains("?")) {
            String queryLine = requestLine[1].split("\\?")[1];
            for (String s : queryLine.split("&")) {
                s = s.trim();
                String[] split = s.split("=");

                query.put(split[0], split[1]);
            }
        }

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

        return new HttpRequest(requestLine[0], requestLine[2], "", requestLine[1], query, sb.toString(), headers);
    }
}
