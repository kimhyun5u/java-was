package codesquad.server.db;

import codesquad.db.Database;
import codesquad.db.DatabaseResolver;
import codesquad.model.Comment;
import codesquad.model.User;

import java.util.List;
import java.util.Objects;

public class CommentRepository {
    private static final String DBNAME = "comments";

    private CommentRepository() {
    }

    public static void addComment(User user, Long pageId, String content) {
        String sql = "INSERT INTO " + DBNAME + " (userId, pageId, username, content) VALUES (?, ?, ?, ?)";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setLong(2, pageId);
            pstmt.setString(3, user.getName());
            pstmt.setString(4, content);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeComment(long id) {
        String sql = "DELETE FROM " + DBNAME + " WHERE id = ?";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Comment> getComments(long id) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE pageId = ?";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeQuery();


            return DatabaseResolver.resultSetToList(pstmt.getResultSet()).stream()
                    .filter(Objects::nonNull)
                    .map(result -> new Comment(result.get("userId".toUpperCase()).toString(), Long.parseLong(result.get("pageId".toUpperCase()).toString()), result.get("username".toUpperCase()).toString(), result.get("content".toUpperCase()).toString()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
