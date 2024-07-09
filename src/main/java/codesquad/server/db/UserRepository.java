package codesquad.server.db;

import codesquad.db.Database;
import codesquad.model.User;

import java.util.Optional;

public class UserRepository {
    private static final String DBNAME = "users";

    private UserRepository() {
    }

    public static void addUser(User user) {
        Database.add(DBNAME, user.getUserId(), user);
    }

    public static Optional<User> getUser(String userId) {
        return Optional.ofNullable((User) Database.get(DBNAME, userId).orElse(null));
    }
}
