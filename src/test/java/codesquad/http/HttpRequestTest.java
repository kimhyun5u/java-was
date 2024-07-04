package codesquad.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {
    InputStream is;

    @BeforeEach
    void setUp() {
        List<String> httpRequest = List.of(
                "GET /index.html HTTP/1.1",
                "Host: www.example.com",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                "Accept-Language: en-US,en;q=0.5",
                "Accept-Encoding: gzip, deflate, br",
                "Connection: keep-alive",
                "Upgrade-Insecure-Requests: 1",
                "Cache-Control: max-age=0",
                "");
        String joinedString = String.join("\r\n", httpRequest);
        is = new ByteArrayInputStream(joinedString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("요청에 맞게 매핑이 된다.")
    void checkRequestMapping() throws IOException {
        HttpRequest request = HttpRequestParser.parse(is);

        assertEquals("GET", request.getMethod());
        assertEquals("/index.html", request.getPath());
    }

}
