package codesquad.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceResolver {
    private ResourceResolver() {
    }

    public static byte[] readResourceFileAsBytes(String resourcePath) {
        try (InputStream is = ResourceResolver.class.getResourceAsStream(resourcePath);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileExtension(String path) {
        int lastIndexOf = path.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자가 없는 경우
        }
        return path.substring(lastIndexOf + 1);
    }
}
