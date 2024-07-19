package codesquad.server.db;

import codesquad.utils.DatabaseResolver;

import java.sql.Connection;
import java.util.Random;

public class JdbcSessionRepository implements SessionRepository {
    private static final String DBNAME = "sessions";
    private static final Random r = new Random();
    private static final int MAX_SESSION_SIZE = 1000;
    private final Connection conn;

    public JdbcSessionRepository(Connection connection) {
        this.conn = connection;
    }

    public int addSession(String userId) {
        String sql = "INSERT INTO " + DBNAME + " (sid, userId) VALUES (?, ?);";
        int sid = r.nextInt(MAX_SESSION_SIZE);
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.setString(2, userId);
            pstmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sid;
    }

    public String getSession(int sid) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE sid = ?;";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.execute();

            var resultList = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            if (resultList.isEmpty()) {
                return null;
            }

            var result = resultList.get(0);

            return result.get("userId").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeSession(int sid) {
        String sql = "DELETE FROM " + DBNAME + " WHERE sid = ?;";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValid(int sid) {
        return getSession(sid) != null;
    }
}
