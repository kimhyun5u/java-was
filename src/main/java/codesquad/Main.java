package codesquad;

import codesquad.http.HttpStatus;
import codesquad.http.Server;
import codesquad.server.handlers.UserHandler;
import codesquad.server.handlers.ViewRegistrationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;


    public static void main(String[] args) {
        Server server = Server.defaultServer(PORT, THREAD_POOL_SIZE);


        server.post("/user/create", UserHandler::createUser);
        server.post("/user/login", UserHandler::login);
        server.get("/register.html", ViewRegistrationHandler::viewRegistration);
        server.get("/", ViewRegistrationHandler::getIndexPage);
        server.post("/user/logout", UserHandler::logout);
        server.get("/user/login_failed", (ctx) -> {
            ctx.response()
                    .setStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "text/html")
                    .setBody("Login failed".getBytes());
        });
        server.get("/user/logout_failed", (ctx) -> {
            ctx.response()
                    .setStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "text/html")
                    .setBody("Logout failed".getBytes());
        });
        server.staticFiles("/", "/static");

        try {
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start HTTP server", e);
        }
    }
}
