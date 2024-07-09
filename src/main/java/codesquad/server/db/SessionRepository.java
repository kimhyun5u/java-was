package codesquad.server.db;


import codesquad.db.Database;

import java.util.Random;

public class SessionRepository {
    private static final String DBNAME = "sessions";
    private static final Random r = new Random();
    private static final int MAX_SESSION_SIZE = 1000;

    private SessionRepository() {
    }

    public static int addSession(String userId) {
        int sid = r.nextInt(MAX_SESSION_SIZE);
        Database.add(DBNAME, sid, userId);
        return sid;
    }

    public static String getSession(int sid) {
        return (String) Database.get(DBNAME, sid).orElse(null);
    }

    public static void removeSession(int sid) {
        Database.remove(DBNAME, sid);
    }

    public static boolean isValid(int sid) {
        return Database.get(DBNAME, sid).isPresent();
    }
}
