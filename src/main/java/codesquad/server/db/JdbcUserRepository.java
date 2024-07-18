package codesquad.server.db;

import codesquad.db.DatabaseResolver;
import codesquad.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
    private static final String DBNAME = "users";
    private final Connection conn;

    public JdbcUserRepository(Connection conn) {
        this.conn = conn;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO " + DBNAME + " (userId, password, name, email) VALUES (?, ?, ?, ?);";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> getUser(String userId) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE userId = ?;";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.execute();

            var resultList = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            if (resultList.isEmpty()) {
                return Optional.empty();
            }

            var result = resultList.get(0);

            return Optional.of(new User(result.get("userId").toString(), result.get("password").toString(), result.get("name").toString(), result.get("email").toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers() {
        String sql = "SELECT * FROM " + DBNAME;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            List<Map<String, Object>> results = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            return results.stream()
                    .map(result -> new User(result.get("userId").toString(), result.get("password").toString(), result.get("name").toString(), result.get("email").toString()))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
