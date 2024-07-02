package codesquad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String BASE_RESOURCE_PATH = "build/resources/main/";
    private static final int THREAD_POOL_SIZE = 10; // 동시 요청 수에 맞춰 조정

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

            String path = "/index.html";

            String requestLine = br.readLine();
            if (requestLine != null && requestLine.startsWith("GET")) {
                path = requestLine.split(" ")[1];
                logger.info(requestLine);
            }


            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            String fullPath = BASE_RESOURCE_PATH + "static" + path;
            File file = new File(fullPath);

            if (file.exists()) {
                String contentType = getContentType(path);
                byte[] content = readFileAt(fullPath);

                clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                clientOutput.write(("Content-Type: " + contentType + "\r\n").getBytes());
                clientOutput.write(("Content-Length: " + content.length + "\r\n").getBytes());
                clientOutput.write("\r\n".getBytes());
                clientOutput.write(content);
            } else {
                String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
                clientOutput.write(notFoundResponse.getBytes());
            }
            clientOutput.flush();
        } catch (IOException e) {
            logger.error("Error handling request", e);
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
