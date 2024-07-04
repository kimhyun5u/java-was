package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {
    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        // Request Line Parser
        String[] requestLine = lines.get(0).split(" ");
        int headerCounter = 1;
        Map<String, String> query = new HashMap<>();
        String target;
        requestLine[1] = URLDecoder.decode(requestLine[1], "UTF-8");
        if (requestLine[1].contains("?")) {
            String[] requests = requestLine[1].split("\\?");
            target = requests[0];
            String queryLine = requests[1];
            for (String s : queryLine.split("&")) {
                s = s.trim();
                String[] split = s.split("=");

                query.put(split[0], split[1]);
            }
        } else {
            target = requestLine[1];
        }

        // Header Parse
        Map<String, String> headers = new HashMap<>();
        while (headerCounter < lines.size() && !lines.get(headerCounter).isEmpty()) {
            String[] header = lines.get(headerCounter++).split(":");
            headers.put(header[0].trim(), header[1].trim());
        }

        // Body Parse
        String body = "";
        if (headers.get("Content-Length") != null) {
            lines.clear();
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                lines.add(line);
            }

            StringBuilder bodyBuilder = new StringBuilder();
            for (String l : lines) {
                bodyBuilder.append(l);
                bodyBuilder.append("\n");
            }
            body = bodyBuilder.toString();
        }


        return new HttpRequest(requestLine[0], requestLine[2], body, target, query, headers);
    }
}
