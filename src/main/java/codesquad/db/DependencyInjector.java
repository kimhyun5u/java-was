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
            Connection connection = CsvDatabaseManager.getConnection();

            // 초기 디비 설정
            connection.createStatement().execute("CREATE TABLE users (userId VARCHAR(255) PRIMARY KEY, password VARCHAR(255), name VARCHAR(255), email VARCHAR(255));");
            connection.createStatement().execute("CREATE TABLE sessions (sid INT PRIMARY KEY, userId VARCHAR(255));");
            connection.createStatement().execute("CREATE TABLE articles (id INT PRIMARY KEY AUTO_INCREMENT, userId VARCHAR(255),username VARCHAR(255), content TEXT, uploadImgPath VARCHAR(255), originalImgName VARCHAR(255), imgSrc VARCHAR(255));");
            connection.createStatement().execute("CREATE TABLE comments (id INT PRIMARY KEY AUTO_INCREMENT , userId VARCHAR(255), pageId INT, username VARCHAR(255), content TEXT);");

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
