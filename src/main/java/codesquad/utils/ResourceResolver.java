package codesquad.utils;

import codesquad.http.File;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

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

    public static File uploadFile(File multipartFile) {
        String uploadName = UUID.randomUUID() + multipartFile.getName();
        URL rootURL = ResourceResolver.class.getResource(multipartFile.getUploadPath());
        if (rootURL == null) {
            throw new RuntimeException("Resource not found: " + multipartFile.getUploadPath());
        }

        String uploadPath = rootURL.getPath() + uploadName;

        // 파일을 저장하는 로직
        try (RandomAccessFile file = new RandomAccessFile(uploadPath, "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.wrap(multipartFile.getContent());
            channel.write(buffer);

            multipartFile.setUploadName(uploadName);
            multipartFile.setUploadPath(uploadPath);

            return multipartFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
