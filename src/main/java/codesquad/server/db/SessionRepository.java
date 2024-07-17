package codesquad.server.db;

public interface SessionRepository {
    int addSession(String userId);

    String getSession(int sid);

    void removeSession(int sid);

    boolean isValid(int sid);
}
