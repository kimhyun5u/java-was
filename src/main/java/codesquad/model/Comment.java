package codesquad.model;

import java.util.Date;

public class Comment {
    private final User user;
    private final String content;
    private final Date created;

    public Comment(User user, String content) {
        this.user = user;
        this.content = content;
        this.created = new Date();
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
}
