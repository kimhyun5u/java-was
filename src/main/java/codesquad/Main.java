package codesquad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int THREAD_POOL_SIZE = 10;
    private static final String ROOT_PATH = "/index.html";
    private static final int PORT = 8080;
    private static final String STATIC_RESOURCE_PATH = "/static";


    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));

            logger.info("Listening for connection on port 8080 ....");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleRequest(clientSocket));
            }
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (clientSocket;
             BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream clientOutput = clientSocket.getOutputStream()) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                lines.add(line);
            }

            HttpRequest request = HttpRequest.of(lines);
            String path = request.getPath();

            logger.info(request.getRequestLine());
            if (path.equals("/") || path.isEmpty()) {
                path = ROOT_PATH;
            }

            String resourcePath = STATIC_RESOURCE_PATH + path;
            byte[] content;
            try {
                content = readResourceFileAsBytes(resourcePath);
            } catch (IOException e) {
                String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
                clientOutput.write(notFoundResponse.getBytes());
                clientOutput.flush();
                return;
            }

            String contentType = getContentType(path);
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

    private static byte[] readResourceFileAsBytes(String resourcePath) throws IOException {
        try (InputStream is = Main.class.getResourceAsStream(resourcePath);
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

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".jpeg") || path.endsWith(".jpg")) return "image/jpeg";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
