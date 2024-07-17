package codesquad;

import codesquad.db.DependencyInjector;
import codesquad.http.HttpStatus;
import codesquad.http.Server;
import codesquad.server.handlers.ArticleHandler;
import codesquad.server.handlers.UserHandler;
import codesquad.server.handlers.ViewHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;


    public static void main(String[] args) {
        DependencyInjector.initialize();

        UserHandler userHandler = DependencyInjector.getUserHandler();
        ArticleHandler articleHandler = DependencyInjector.getArticleHandler();
        ViewHandler viewHandler = DependencyInjector.getViewHandler();

        Server server = Server.defaultServer(PORT, THREAD_POOL_SIZE);

        server.post("/user/create", userHandler::createUser);
        server.post("/user/login", userHandler::login);
        server.get("/register.html", viewHandler::getRegistrationPage);
        server.get("/", viewHandler::getIndexPage);
        server.post("/user/logout", userHandler::logout);
        server.get("/user/list", viewHandler::getUserListPage);
        server.get("/write", viewHandler::getWritePage);
        server.post("/write", articleHandler::write);
        server.post("/comment", articleHandler::addComment);

        server.get("/user/login_failed", ctx ->
                ctx.response()
                        .setStatus(HttpStatus.OK)
                        .addHeader("Content-Type", "text/html")
                        .setBody("Login failed".getBytes()));
        server.get("/user/logout_failed", ctx ->
                ctx.response()
                        .setStatus(HttpStatus.OK)
                        .addHeader("Content-Type", "text/html")
                        .setBody("Logout failed".getBytes()));
        server.staticFiles("/", "/static");


        server.start();

    }
}
