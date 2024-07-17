package codesquad.server.db;

public class TestSessionRepository implements SessionRepository {
    @Override
    public int addSession(String userId) {
        return 0;
    }

    @Override
    public String getSession(int sid) {
        return "";
    }

    @Override
    public void removeSession(int sid) {

    }

    @Override
    public boolean isValid(int sid) {
        return false;
    }
}
