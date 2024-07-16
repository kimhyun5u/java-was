package codesquad.server.db;

import codesquad.db.Database;
import codesquad.db.DatabaseResolver;
import codesquad.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserRepository {
    private static final String DBNAME = "users";

    private UserRepository() {
    }

    public static void addUser(User user) {
        String sql = "INSERT INTO " + DBNAME + " (userId, password, name, email) VALUES (?, ?, ?, ?)";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> getUser(String userId) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE userId = ?";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.executeQuery();

            var resultList = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            if (resultList.isEmpty()) {
                return Optional.empty();
            }

            var result = resultList.get(0);

            return Optional.of(new User(result.get("userId".toUpperCase()).toString(), result.get("password".toUpperCase()).toString(), result.get("name".toUpperCase()).toString(), result.get("email".toUpperCase()).toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getUsers() {
        String sql = "SELECT * FROM " + DBNAME;
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.executeQuery();
            List<Map<String, Object>> results = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            return results.stream()
                    .map(result -> new User(result.get("userId".toUpperCase()).toString(), result.get("password".toUpperCase()).toString(), result.get("name".toUpperCase()).toString(), result.get("email".toUpperCase()).toString()))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
