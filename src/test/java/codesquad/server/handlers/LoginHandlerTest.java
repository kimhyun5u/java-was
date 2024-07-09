package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginHandlerTest {

    InputStream is;
    String request = """
            POST /user/login HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: 93
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            userId=javajigi&password=password
                        
            """;

    @BeforeEach
    void setUp() {
        is = new ByteArrayInputStream(request.getBytes());
    }

    @Test
    void testLogin() throws IOException {
        // given
        HttpRequest req = HttpRequest.from(is);
        HttpResponse res = new HttpResponse();

        Context ctx = new Context(req, res);

        // when
        UserLoginHandler.login(ctx);

        // then
        assertEquals(HttpStatus.OK.getCode(), res.getStatusCode());
    }
}
