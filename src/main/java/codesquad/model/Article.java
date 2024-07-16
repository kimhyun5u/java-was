package codesquad.model;

public class Article {

    private final String userId;
    private final String username;
    private Long id;
    private final String content;

    public Article(String userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
    }

    public Article(Long id, String userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}

