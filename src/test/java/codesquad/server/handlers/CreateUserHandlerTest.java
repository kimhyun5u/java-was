package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.model.User;
import codesquad.utils.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateUserHandlerTest {
    String request = """
            POST /user/create HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: 93
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
                        
                        
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

        assertEquals(HttpStatus.REDIRECT_FOUND.getCode(), res.getStatusCode());
    }

}
