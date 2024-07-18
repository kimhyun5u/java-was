package codesquad.server.db;

import codesquad.db.DatabaseResolver;
import codesquad.model.Comment;
import codesquad.model.User;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class JdbcCommentRepository implements CommentRepository {
    private static final String DBNAME = "comments";
    private final Connection conn;

    public JdbcCommentRepository(Connection connection) {
        this.conn = connection;
    }

    public void addComment(User user, Long pageId, String content) {
        String sql = "INSERT INTO " + DBNAME + " (userId, pageId, username, content) VALUES (?, ?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setLong(2, pageId);
            pstmt.setString(3, user.getName());
            pstmt.setString(4, content);
            pstmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeComment(long id) {
        String sql = "DELETE FROM " + DBNAME + " WHERE id = ?";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Comment> getComments(long id) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE pageId = ?";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.execute();


            return DatabaseResolver.resultSetToList(pstmt.getResultSet()).stream()
                    .filter(Objects::nonNull)
                    .map(result -> new Comment(result.get("userId").toString(), Long.parseLong(result.get("pageId").toString()), result.get("username").toString(), result.get("content").toString()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
