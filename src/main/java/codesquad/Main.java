package codesquad;

import codesquad.http.Server;
import codesquad.server.handlers.CreateUserHandler;
import codesquad.server.handlers.ViewRegistrationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;


    public static void main(String[] args) {
        Server server = new Server(PORT, THREAD_POOL_SIZE);

        server.post("/user/create", CreateUserHandler::createUser);
        server.get("/register.html", ViewRegistrationHandler::viewRegistration);
        server.staticFiles("/", "/static");

        try {
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start HTTP server", e);
        }
    }
}
