package codesquad.server.db;

import codesquad.model.User;

import java.util.List;
import java.util.Optional;

public class TestUserRepository implements UserRepository {

    @Override
    public void addUser(User user) {

    }

    @Override
    public Optional<User> getUser(String userId) {
        return Optional.empty();
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }
}
