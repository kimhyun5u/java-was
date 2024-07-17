package codesquad.server.db;

import java.util.HashMap;
import java.util.Map;

public class TestSessionRepository implements SessionRepository {
    private final Map<Object, Object> db = new HashMap<>();
    private int sid = 1;

    @Override
    public int addSession(String userId) {
        db.put(sid, userId);

        return sid++;
    }

    @Override
    public String getSession(int sid) {
        return (String) db.get(sid);
    }

    @Override
    public void removeSession(int sid) {
        db.remove(sid);
    }

    @Override
    public boolean isValid(int sid) {
        return db.containsKey(sid);
    }
}
