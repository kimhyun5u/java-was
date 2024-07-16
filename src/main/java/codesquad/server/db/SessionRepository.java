package codesquad.server.db;


import codesquad.db.Database;
import codesquad.db.DatabaseResolver;

import java.util.Random;

public class SessionRepository {
    private static final String DBNAME = "sessions";
    private static final Random r = new Random();
    private static final int MAX_SESSION_SIZE = 1000;

    private SessionRepository() {
    }

    public static int addSession(String userId) {
        String sql = "INSERT INTO " + DBNAME + " (sid, userId) VALUES (?, ?)";
        int sid = r.nextInt(MAX_SESSION_SIZE);
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sid;
    }

    public static String getSession(int sid) {
        String sql = "SELECT * FROM " + DBNAME + " WHERE sid = ?";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.executeQuery();

            var resultList = DatabaseResolver.resultSetToList(pstmt.getResultSet());
            if (resultList.isEmpty()) {
                return null;
            }

            var result = resultList.get(0);

            return result.get("userId".toUpperCase()).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeSession(int sid) {
        String sql = "DELETE FROM " + DBNAME + " WHERE sid = ?";
        try (var pstmt = Database.getPreparedStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValid(int sid) {
        return getSession(sid) != null;
    }
}
