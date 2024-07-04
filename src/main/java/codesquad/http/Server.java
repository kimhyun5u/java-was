package codesquad.http;

import codesquad.http.handler.Handler;
import codesquad.http.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final int threadPoolSize;
    private final ExecutorService threadPool;
    private final Router router;

    public Server(int port, int threadPoolSize) {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
        this.router = new Router();
    }

    public void get(String path, Handler handler) {
        addRoute("GET", path, handler);
    }

    public void post(String path, Handler handler) {
        addRoute("POST", path, handler);
    }

    public void staticFiles(String path, String staticPath) {
        router.staticFiles(path, staticPath);
    }

    private void addRoute(String method, String path, Handler handler) {
        router.addRoute(method, path, handler);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));

            // init MIME
            MIME.init();

            logger.info("Listening for connection on port {} ....", port);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleRequest(clientSocket));
            }
        }
    }

    private void handleRequest(Socket clientSocket) {
        try (clientSocket; var input = clientSocket.getInputStream(); var output = clientSocket.getOutputStream()) {
            HttpRequest req = HttpRequestParser.parse(input);
            HttpResponse res = new HttpResponse(output);

            Context ctx = new Context(req, res);

            Handler handler = router.getHandlers(req.getMethod(), req.getPath());

            logger.info(req.getRequestLine());

            if (handler != null) {
                handler.handle(ctx);
            } else {
                res.setStatus(HttpStatus.BAD_REQUEST);
                res.setVersion(req.getVersion());
            }

            res.send();
        } catch (IOException e) {
            logger.error("Error Handling Request", e);
        }
    }
}
