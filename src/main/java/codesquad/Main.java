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
    private static final String BASE_RESOURCE_PATH = "build/resources/main/";
    private static final int THREAD_POOL_SIZE = 10;
    private static final String ROOT_PATH = "/index.html";

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            logger.info("Listening for connection on port 8080 ....");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleRequest(clientSocket));
            }
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (clientSocket; BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); OutputStream clientOutput = clientSocket.getOutputStream()) {
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

            String fullPath = BASE_RESOURCE_PATH + "static" + path;
            File file = new File(fullPath);

            if (file.exists()) {
                String contentType = getContentType(path);
                byte[] content = readFileAt(fullPath);
                String connectionHeader = request.getHeader("Connection");
                boolean keepAlive = "keep-alive".equalsIgnoreCase(connectionHeader);

                clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                clientOutput.write(("Content-Type: " + contentType + "\r\n").getBytes());
                clientOutput.write(("Content-Length: " + content.length + "\r\n").getBytes());
                if (keepAlive) {
                    clientOutput.write(("Connection: keep-alive\r\n").getBytes());
                } else {
                    clientOutput.write(("Connection: close\r\n").getBytes());
                }
                clientOutput.write("\r\n".getBytes());
                clientOutput.write(content);
            } else {
                String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
                clientOutput.write(notFoundResponse.getBytes());
            }
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

    private static byte[] readFileAt(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
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
