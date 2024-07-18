package codesquad.server.db;

import codesquad.db.DatabaseResolver;
import codesquad.model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcArticleRepository implements ArticleRepository {
    private static final String DBNAME = "articles";
    private Connection conn;

    public JdbcArticleRepository(Connection conn) {
        this.conn = conn;
    }

    public void addArticle(Article article) {
        String sql = "INSERT INTO " + DBNAME + " (userId, username, content, uploadPath, originalName) VALUES ( ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getUserId());
            pstmt.setString(2, article.getUsername());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getUploadPath());
            pstmt.setString(5, article.getOriginalName());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

            return new Article(Long.parseLong(result.get("id".toUpperCase()).toString()), result.get("userId".toUpperCase()).toString(), result.get("username".toUpperCase()).toString(), result.get("content".toUpperCase()).toString(), result.get("uploadPath".toUpperCase()).toString(), result.get("originalName".toUpperCase()).toString());
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
