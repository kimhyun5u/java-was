package codesquad.model;

public class Article {

    private Long id;
    private final String userId;
    private final String username;
    private final String content;
    private String uploadPath;
    private String originalName;

    public Article(String userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
    }

    public Article(Long id, String userId, String username, String content, String uploadPath, String originalName) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.id = id;
        this.uploadPath = uploadPath;
        this.originalName = originalName;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}

