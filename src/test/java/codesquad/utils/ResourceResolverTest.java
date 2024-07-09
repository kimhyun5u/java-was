package codesquad.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceResolverTest {

    @Test
    @DisplayName("확장자 확인 테스트")
    void testGetFileExtension() {
        assertEquals("txt", ResourceResolver.getFileExtension("file.txt"));
        assertEquals("", ResourceResolver.getFileExtension("file"));
        assertEquals("gz", ResourceResolver.getFileExtension("file.gz"));
    }

    @Test
    @DisplayName("파일을 잘 불러오는지 테스트")
    void testReadResourceFileAsBytes() {
        byte[] file = ResourceResolver.readResourceFileAsBytes("/static/index.html");
        assertNotNull(file);
        assertTrue(file.length > 0);
    }

    @Test
    @DisplayName("없는 파일 예외 처리 테스트")
    void testReadResourceFileAsBytesNonExistent() {
        assertThrows(RuntimeException.class, () -> ResourceResolver.readResourceFileAsBytes("/static/main.html"));

    }

}
