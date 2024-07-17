package codesquad.server.db;

import codesquad.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestUserRepository implements UserRepository {
    private Map<String, Object> db = new HashMap<>();

    @Override
    public void addUser(User user) {
        db.put(user.getUserId(), user);
    }

    @Override
    public Optional<User> getUser(String userId) {
        if (db.containsKey(userId)) {
            return Optional.of((User) db.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public List<User> getUsers() {
        return db.values().stream().map(user -> (User) user).toList();
    }
}
