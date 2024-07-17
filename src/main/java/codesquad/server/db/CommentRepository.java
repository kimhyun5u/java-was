package codesquad.server.db;

import codesquad.model.Comment;
import codesquad.model.User;

import java.util.List;

public interface CommentRepository {
    void addComment(User user, Long pageId, String content);

    void removeComment(long id);

    List<Comment> getComments(long id);
}
