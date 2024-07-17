package codesquad.db;

import codesquad.server.db.*;
import codesquad.server.handlers.ArticleHandler;
import codesquad.server.handlers.UserHandler;
import codesquad.server.handlers.ViewHandler;
import codesquad.utils.AuthenticationResolver;

import java.sql.Connection;
import java.sql.SQLException;

public class DependencyInjector {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DependencyInjector.class);
    private static UserHandler userHandler;
    private static ArticleHandler articleHandler;
    private static ViewHandler viewHandler;

    private DependencyInjector() {
    }

    public static void initialize() {
        try {
            Connection connection = DatabaseManager.getConnection();
            UserRepository userRepository = new JdbcUserRepository(connection);
            ArticleRepository articleRepository = new JdbcArticleRepository(connection);
            CommentRepository commentRepository = new JdbcCommentRepository(connection);
            SessionRepository sessionRepository = new JdbcSessionRepository(connection);
            AuthenticationResolver authenticationResolver = new AuthenticationResolver(userRepository, sessionRepository);
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
