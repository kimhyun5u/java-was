package codesquad.db;

import codesquad.server.db.ArticleRepository;
import codesquad.server.db.CommentRepository;
import codesquad.server.db.SessionRepository;
import codesquad.server.db.UserRepository;
import codesquad.server.handlers.ArticleHandler;
import codesquad.server.handlers.UserHandler;
import codesquad.server.handlers.ViewHandler;
import codesquad.utils.AuthenticationResolver;

import java.sql.Connection;
import java.sql.SQLException;

public class DependencyInjector {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DependencyInjector.class);
    private static Connection connection;
    private static UserRepository userRepository;
    private static ArticleRepository articleRepository;
    private static CommentRepository commentRepository;
    private static SessionRepository sessionRepository;
    private static UserHandler userHandler;
    private static ArticleHandler articleHandler;
    private static ViewHandler viewHandler;
    private static AuthenticationResolver authenticationResolver;

    private DependencyInjector() {
    }

    public static void initialize() {
        try {
            connection = DatabaseManager.getConnection();
            userRepository = new UserRepository(connection);
            articleRepository = new ArticleRepository(connection);
            commentRepository = new CommentRepository(connection);
            sessionRepository = new SessionRepository(connection);
            userHandler = new UserHandler(userRepository, sessionRepository);
            articleHandler = new ArticleHandler(articleRepository, commentRepository, authenticationResolver);
            viewHandler = new ViewHandler(userRepository, sessionRepository, articleRepository, commentRepository);
        } catch (SQLException e) {
            logger.error("Failed to initialize database connection", e);
        }
    }

    public static UserHandler getUserHandler() {
        return userHandler;
    }

    public static ArticleHandler getArticleHandler() {
        return articleHandler;
    }

    public static ViewHandler getViewHandler() {
        return viewHandler;
    }
}
