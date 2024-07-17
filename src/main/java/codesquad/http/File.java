package codesquad.http;

public class File {
    private final String name;
    private final byte[] content;

    public File(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
