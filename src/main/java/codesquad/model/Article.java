package codesquad.model;

import java.util.Date;

public class Article {
    private final User user;
    private final String content;
    private final Date created;

    public Article(User user, String content) {
        this.user = user;
        this.content = content;
        this.created = new Date();
    }
}

