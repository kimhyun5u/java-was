package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.model.User;
import codesquad.utils.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CreateUserHandlerTest {
    String request = """
            GET /create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
            Accept-Encoding: gzip, deflate, br, zstd
            Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
            Cache-Control: no-cache
            Connection: keep-alive
            Host: localhost:8080
            Pragma: no-cache
            Sec-Fetch-Dest: document
            Sec-Fetch-Mode: navigate
            Sec-Fetch-Site: none
            Sec-Fetch-User: ?1
            Upgrade-Insecure-Requests: 1
            User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36
            sec-ch-ua: "Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126"
            sec-ch-ua-mobile: ?0
            sec-ch-ua-platform: "macOS"
                        
            """;
    byte[] user;
    InputStream is;

    @BeforeEach
    void setUp() {
        is = new ByteArrayInputStream(request.getBytes());
        user = JsonConverter.toJson(new User("javajigi", "password", "박재성")).getBytes();
    }

    @Test
    void createUser() throws IOException {
        HttpRequest req = HttpRequest.from(is);
        HttpResponse res = new HttpResponse();
        Context ctx = new Context(req, res);

        CreateUserHandler.createUser(ctx);

        assertArrayEquals(user, res.getBody());
    }

}
