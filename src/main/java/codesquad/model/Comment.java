package codesquad.model;

public class Comment {
    private final String userId;
    private final Long pageId;
    private final String username;
    private final String content;

    public Comment(String userId, Long pageId, String username, String content) {
        this.userId = userId;
        this.pageId = pageId;
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
