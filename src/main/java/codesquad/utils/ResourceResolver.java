package codesquad.utils;

import codesquad.http.File;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ResourceResolver {
    private static final Path EXTERNAL_RESOURCE_DIR = Paths.get(System.getProperty("user.home"), "/app_resources/");

    private ResourceResolver() {
    }

    static {
        try {
            initializeExternalResourceDir();
        } catch (IOException e) {
            System.err.println("Failed to initialize external resource directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeExternalResourceDir() throws IOException {
        if (!Files.exists(EXTERNAL_RESOURCE_DIR)) {
            Files.createDirectories(EXTERNAL_RESOURCE_DIR);
        }
    }


    public static byte[] readResourceFileAsBytes(String resourcePath) {
        // 내부에서 찾기
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
            // 외부에서 찾기
            try (RandomAccessFile file = new RandomAccessFile(EXTERNAL_RESOURCE_DIR + resourcePath, "r");
                 FileChannel channel = file.getChannel()) {
                ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
                channel.read(buffer);
                return buffer.array();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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

        multipartFile.setImageSrc(multipartFile.getImageSrc() + uploadName);

        String uploadPath = EXTERNAL_RESOURCE_DIR + multipartFile.getUploadPath();

        // 없으면 디렉토리 생성
        if (!Files.exists(Paths.get(uploadPath))) {
            try {
                Files.createDirectories(Paths.get(uploadPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        uploadPath += uploadName;

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
