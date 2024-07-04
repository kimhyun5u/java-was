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

    public HttpResponse() {
        this.headers = new HashMap<>();
    }

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HashMap<>();
    }

    public HttpResponse setStatus(HttpStatus status) {
        this.statusCode = status.getCode();
        this.statusMsg = status.getMessage();
        return this;
    }

    public HttpResponse addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpResponse setVersion(String version) {
        this.version = version;
        return this;
    }

    public HttpResponse setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public void send() throws IOException {
        String statusLine = String.format("%s %d %s", version, statusCode, statusMsg);
        outputStream.write(statusLine.getBytes());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            String headerLine = header.getKey() + ": " + header.getValue() + " \r\n";
            outputStream.write(headerLine.getBytes());
        }

        outputStream.write("\r\n".getBytes());

        if (body != null) {
            outputStream.write(body);
        }

        outputStream.flush();
    }

    public byte[] getBody() {
        return this.body;
    }
}
