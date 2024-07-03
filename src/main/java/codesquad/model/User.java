package codesquad.model;

public class User {
    private String userId;
    private String password;
    private String name;

    public User(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public String toJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"userId\":\"").append(userId).append("\",");
        builder.append("\"password\":\"").append(password).append("\",");
        builder.append("\"name\":\"").append(name).append("\"");
        builder.append("}");
        return builder.toString();
    }
}
