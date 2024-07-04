package codesquad.http.router;

import codesquad.http.handler.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {
    Router r;

    @BeforeEach
    void setUp() {
        r = new Router();
    }

    @Test
    @DisplayName("루트 등록 및 핸들러 확인 테스트")
    void testAddRouteAndGetHandler() {
        Handler handler = ctx -> {
        };
        r.addRoute("GET", "/test", handler);

        Handler result = r.getHandlers("GET", "/test");
        assertSame(handler, result);
    }

    @Test
    @DisplayName("없는 루트 핸들러 테스트")
    void testGetHandlerForNonExistentRoute() {
        Handler result = r.getHandlers("GET", "/test");
        assertNull(result);
    }

    @Test
    @DisplayName("확장자 확인 테스트")
    void testGetFileExtension() {
        assertEquals("txt", r.getFileExtension("file.txt"));
        assertEquals("", r.getFileExtension("file"));
        assertEquals("gz", r.getFileExtension("file.gz"));
    }

    @Test
    @DisplayName("파일을 잘 불러오는지 테스트")
    void testReadResourceFileAsBytes() throws IOException {
        byte[] file = r.readResourceFileAsBytes("/static/index.html");
        assertNotNull(file);
        assertTrue(file.length > 0);
    }

    @Test
    @DisplayName("없는 파일 예외 처리 테스트")
    void testReadResourceFileAsBytesNonExistent() {
        assertThrows(IOException.class, () -> r.readResourceFileAsBytes("/static/main.html"));

    }
}
