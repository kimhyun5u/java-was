package codesquad.http.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class DefaultRequestHandlerTest {
    private DefaultRequestHandler requestHandler;
    private byte[] indexHtmlContent;

    @BeforeEach
    void setUp() throws IOException {
        requestHandler = new DefaultRequestHandler();
        String resourcePath = "/static/index.html";
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Resource not found: " + resourcePath);
            indexHtmlContent = inputStream.readAllBytes();
        }
    }

    @Test
    void testReadExistingResourceFile() throws IOException {
        byte[] content = requestHandler.readResourceFileAsBytes("/static/index.html");
        assertArrayEquals(indexHtmlContent, content, "The content of index.html should match");
    }

    @Test
    void testReadNonExistentResourceFile() {
        assertThrows(IOException.class, () ->
                        requestHandler.readResourceFileAsBytes("/static/nonexistent.html"),
                "Should throw IOException for non-existent file"
        );
    }

    @Test
    void testGetFileExtension() {
        assertEquals("html", requestHandler.getFileExtension("/static/index.html"));
    }
}
