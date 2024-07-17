package codesquad.server.db;

import codesquad.db.DatabaseResolver;
import codesquad.model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class JdbcArticleRepository implements ArticleRepository {
    private static final AtomicLong id = new AtomicLong(1);
    private static final String DBNAME = "articles";
    private Connection conn;

    public JdbcArticleRepository(Connection conn) {
        this.conn = conn;
    }

    public void addArticle(Article article) {
        String sql = "INSERT INTO " + DBNAME + " (id, userId, username, content) VALUES (?, ?, ?, ?)";
        article.setId(id.get());
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, article.getId());
            pstmt.setString(2, article.getUserId());
            pstmt.setString(3, article.getUsername());
            pstmt.setString(4, article.getContent());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        id.incrementAndGet();
    }

    public Article getArticle(long id) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeQuery();

            var resultList = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            if (resultList.isEmpty()) {
                return null;
            }

            var result = resultList.get(0);

            return new Article(Long.parseLong(result.get("id".toUpperCase()).toString()), result.get("userId".toUpperCase()).toString(), result.get("username".toUpperCase()).toString(), result.get("content".toUpperCase()).toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Article article) {
        String sql = "UPDATE " + DBNAME + " SET content = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getContent());
            pstmt.setLong(2, article.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
