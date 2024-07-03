package codesquad.http.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpRequestParser;
import codesquad.http.MIME;
import codesquad.model.User;
import codesquad.utils.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DefaultRequestHandler implements RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRequestHandler.class);

    private static final String ROOT_PATH = "/index.html";
    private static final String STATIC_RESOURCE_PATH = "/static";

    protected byte[] readResourceFileAsBytes(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        }
    }

    protected String getFileExtension(String path) {
        int lastIndexOf = path.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자가 없는 경우
        }
        return path.substring(lastIndexOf + 1);
    }

    @Override
    public void handleRequest(Socket clientSocket) {
        try (clientSocket;
             BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream clientOutput = clientSocket.getOutputStream()) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                lines.add(line);
            }

            HttpRequest request = HttpRequestParser.parse(lines);
            if (request.getHeader("Content-Length") != null) {
                lines.clear();
                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    lines.add(line);
                }
                request.setBody(HttpRequestParser.parseBody(lines));
            }

            String path = request.getTarget();

            logger.info(request.getRequestLine());
            if (path.equals("/")) {
                path = ROOT_PATH;
            } else if ("".equals(getFileExtension(path))) {
                path += ROOT_PATH;
            }

            String resourcePath = STATIC_RESOURCE_PATH + path;
            byte[] content;
            try {
                content = readResourceFileAsBytes(resourcePath);
            } catch (IOException e) {
                if ("/create".equals(request.getTarget())) {
                    User user = new User(request.getQuery("userId"), request.getQuery("password"), request.getQuery("name"));
                    String json = JsonConverter.toJson(user);
                    StringBuilder sb = new StringBuilder();
                    sb.append("HTTP/1.1 201 Created\r\n");
                    sb.append("Content-Type: application/json\r\n");
                    sb.append("Content-Length: ").append(json.getBytes().length).append("\r\n");
                    sb.append("\r\n");
                    sb.append(json);
                    clientOutput.write(sb.toString().getBytes());
                    clientOutput.flush();

                    return;
                } else {
                    String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
                    clientOutput.write(notFoundResponse.getBytes());
                    clientOutput.flush();
                    return;
                }
            }

            String ext = getFileExtension(path);
            String contentType;
            if (ext.isEmpty()) {
                contentType = null;
            } else {
                contentType = MIME.getMIMEType(ext);
            }

            String connectionHeader = request.getHeader("Connection");
            boolean keepAlive = "keep-alive".equalsIgnoreCase(connectionHeader);

            clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
            clientOutput.write(("Content-Type: " + contentType + "\r\n").getBytes());
            clientOutput.write(("Content-Length: " + content.length + "\r\n").getBytes());
            if (keepAlive) {
                clientOutput.write("Connection: keep-alive\r\n".getBytes());
            } else {
                clientOutput.write("Connection: close\r\n".getBytes());
            }
            clientOutput.write("\r\n".getBytes());
            clientOutput.write(content);
            clientOutput.flush();
        } catch (IOException e) {
            logger.error("Error handling request", e);
            try {
                clientSocket.close();
            } catch (IOException closeException) {
                logger.error("Error closing client socket", closeException);
            }
        }
    }
}
