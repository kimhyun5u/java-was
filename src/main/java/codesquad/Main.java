package codesquad;

import codesquad.http.HttpServer;
import codesquad.http.MIME;
import codesquad.http.handler.CreateUserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;


    public static void main(String[] args) {
        MIME.init();
        HttpServer server = new HttpServer(PORT, THREAD_POOL_SIZE);

        server.get("/create", CreateUserHandler::createUser);
        server.staticFiles("/", "/static");

        try {
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start HTTP server", e);
        }
    }
}
