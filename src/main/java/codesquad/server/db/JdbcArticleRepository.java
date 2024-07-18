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
        String sql = "INSERT INTO " + DBNAME + " (userId, username, content, uploadImgPath, originalImgName, imgSrc) VALUES ( ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getUserId());
            pstmt.setString(2, article.getUsername());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getUploadImgPath());
            pstmt.setString(5, article.getOriginalImgName());
            pstmt.setString(6, article.getImgSrc());

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

            long articleId = Long.parseLong(result.get("id".toUpperCase()).toString());
            String userId = result.get("userId".toUpperCase()).toString();
            String username = result.get("username".toUpperCase()).toString();
            String content = result.get("content".toUpperCase()).toString();
            String uploadImgPath = result.get("uploadImgPath".toUpperCase()) == null ? null : result.get("uploadImgPath".toUpperCase()).toString();
            String originalImgName = result.get("originalImgName".toUpperCase()) == null ? null : result.get("originalImgName".toUpperCase()).toString();
            String imgSrc = result.get("imgSrc".toUpperCase()) == null ? null : result.get("imgSrc".toUpperCase()).toString();
            return new Article(articleId, userId, username, content, uploadImgPath, originalImgName, imgSrc);
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
