package codesquad.server.db;

import codesquad.db.DatabaseResolver;
import codesquad.model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class JdbcArticleRepository implements ArticleRepository {
    private static final String DBNAME = "articles";
    private Connection conn;
    private AtomicLong nextId = new AtomicLong(1);
    public JdbcArticleRepository(Connection conn) {
        this.conn = conn;
    }

    public void addArticle(Article article) {
        String sql = "INSERT INTO " + DBNAME + " (id, userId, username, content, uploadImgPath, originalImgName, imgSrc) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nextId.getAndIncrement() + "");
            pstmt.setString(2, article.getUserId());
            pstmt.setString(3, article.getUsername());
            pstmt.setString(4, article.getContent());
            pstmt.setString(5, article.getUploadImgPath());
            pstmt.setString(6, article.getOriginalImgName());
            pstmt.setString(7, article.getImgSrc());

            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Article getArticle(long id) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.execute();

            var resultList = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            if (resultList.isEmpty()) {
                return null;
            }

            var result = resultList.get(0);

            long articleId = Long.parseLong(result.get("id").toString());
            String userId = result.get("userId").toString();
            String username = result.get("username").toString();
            String content = result.get("content").toString();
            String uploadImgPath = result.get("uploadImgPath") == null ? null : result.get("uploadImgPath").toString();
            String originalImgName = result.get("originalImgName") == null ? null : result.get("originalImgName").toString();
            String imgSrc = result.get("imgSrc") == null ? null : result.get("imgSrc").toString();
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
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
