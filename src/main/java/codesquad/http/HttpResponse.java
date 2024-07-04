package codesquad.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private OutputStream outputStream;
    private String version;
    private int statusCode;
    private String statusMsg;
    private Map<String, String> headers;
    private byte[] body;

    private HttpResponse() {
    }

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HashMap<>();
    }

    public void setStatus(HttpStatus status) {
        this.statusCode = status.getCode();
        this.statusMsg = status.getMessage();
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void send() throws IOException {
        String statusLine = String.format("%s %d %s", version, statusCode, statusMsg);
        outputStream.write(statusLine.getBytes());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            String headerLine = String.format("%s: %s\r\n", header.getKey(), header.getValue());
            outputStream.write(headerLine.getBytes());
        }

        outputStream.write("\r\n".getBytes());

        if (body != null) {
            outputStream.write(body);
        }

        outputStream.flush();
    }
}
