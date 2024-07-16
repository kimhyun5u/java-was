package codesquad.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Article {
    private final List<Comment> comments;
    private final User user;
    private final String content;
    private final Date created;
    private long id;
    public Article(User user, String content) {
        this.user = user;
        this.content = content;
        this.created = new Date();
        comments = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Date getCreated() {
        return created;
    }

    public List<Comment> getComments() {
        return comments;
    }
}

