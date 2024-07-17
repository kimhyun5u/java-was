package codesquad.server.db;

import codesquad.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void addUser(User user);

    Optional<User> getUser(String userId);

    List<User> getUsers();
}
