package codesquad.http;

import codesquad.http.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final int port;
    private final int threadPoolSize;
    private final RequestHandler requestHandler;
    private final ExecutorService threadPool;

    public HttpServer(int port, int threadPoolSize, RequestHandler requestHandler) {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
        this.requestHandler = requestHandler;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));

            // init MIME
            MIME.init();

            logger.info("Listening for connection on port {} ....", port);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> requestHandler.handleRequest(clientSocket));
            }
        }
    }
}
