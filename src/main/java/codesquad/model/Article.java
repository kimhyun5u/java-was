package codesquad.model;

public class Article {

    private final String userId;
    private final String username;
    private final String content;
    private Long id;
    private String uploadImgPath;
    private String originalImgName;
    private String imgSrc;

    public Article(String userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
    }

    public Article(Long id, String userId, String username, String content, String uploadPath, String originalImgName, String imgSrc) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.id = id;
        this.uploadImgPath = uploadPath;
        this.originalImgName = originalImgName;
        this.imgSrc = imgSrc;
    }

    public String getUploadImgPath() {
        return uploadImgPath;
    }

    public void setUploadImgPath(String uploadImgPath) {
        this.uploadImgPath = uploadImgPath;
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

    public String getOriginalImgName() {
        return originalImgName;
    }

    public void setOriginalImgName(String originalImgName) {
        this.originalImgName = originalImgName;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}

