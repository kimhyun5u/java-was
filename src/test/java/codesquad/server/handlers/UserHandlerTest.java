package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.model.User;
import codesquad.server.db.TestSessionRepository;
import codesquad.server.db.TestUserRepository;
import codesquad.utils.JsonConverter;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserHandlerTest {
    String createRequest = """
            POST /user/create HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: 93
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net""".replace(System.lineSeparator(), "\r\n");
    String loginRequest = """
            POST /user/login HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: 33
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            userId=javajigi&password=password""".replace(System.lineSeparator(), "\r\n");

    byte[] user;
    InputStream is;
    int sid = 0;
    UserHandler userHandler;
    @BeforeEach
    void setUp() {
        user = JsonConverter.toJson(new User("javajigi", "password", "박재성", "javajigi@slipp.net")).getBytes();
        userHandler = new UserHandler(new TestUserRepository(), new TestSessionRepository());
    }

    @Test
    @Order(1)
    @DisplayName("로그인 실패 - 사용자가 없는 경우")
    void testLoginFailure() throws IOException {
        // given
        is = new ByteArrayInputStream(loginRequest.getBytes());

        HttpRequest req = HttpRequest.from(is);
        HttpResponse res = new HttpResponse();

        Context ctx = new Context(req, res);

        // when
        userHandler.login(ctx);

        // then
        assertEquals("/user/login_failed", res.getHeader("Location"));
    }

    @Test
    @Order(2)
    @DisplayName("회원가입 성공")
    void createUser() throws IOException {
        // given
        is = new ByteArrayInputStream(createRequest.getBytes());
        HttpRequest req = HttpRequest.from(is);
        HttpResponse res = new HttpResponse();
        Context ctx = new Context(req, res);

        // when
        userHandler.createUser(ctx);

        // then
        assertArrayEquals(user, res.getBody());

        assertEquals(HttpStatus.REDIRECT_FOUND.getCode(), res.getStatusCode());
    }

    @Test
    @Order(3)
    @DisplayName("회원가입 후 로그인 성공")
    void testCreateAndLogin() throws IOException {
        // given
        createUser();
        is = new ByteArrayInputStream(loginRequest.getBytes());
        var req = HttpRequest.from(is);
        var res = new HttpResponse();
        var ctx = new Context(req, res);

        // when
        userHandler.login(ctx);

        // then
        assertEquals(HttpStatus.REDIRECT_FOUND.getCode(), res.getStatusCode());

        assertEquals("/", res.getHeader("Location"));
        System.out.println(res.getHeader("set-cookie"));
        sid = Integer.parseInt(res.getHeader("set-cookie").split("=")[1].split(";")[0]);
    }

    @Test
    @Order(4)
    @DisplayName("로그아웃 성공")
    void testLogout() throws IOException {
        // given
        testCreateAndLogin();
        is = new ByteArrayInputStream(("GET /user/logout HTTP/1.1\r\nCookie: sid=" + sid + "\r\n\r\n").getBytes());
        var req = HttpRequest.from(is);
        var res = new HttpResponse();
        var ctx = new Context(req, res);

        // when
        userHandler.logout(ctx);

        // then
        assertEquals(HttpStatus.REDIRECT_FOUND.getCode(), res.getStatusCode());
        assertEquals("/", res.getHeader("Location"));
    }

    @Test
    @Order(5)
    @DisplayName("로그아웃 실패")
    void testLogoutFailure() throws IOException {
        // given
        is = new ByteArrayInputStream(("GET /user/logout HTTP/1.1\r\nCookie: sid=" + sid + "\r\n\r\n").getBytes());
        var req = HttpRequest.from(is);
        var res = new HttpResponse();
        var ctx = new Context(req, res);

        // when
        userHandler.logout(ctx);

        // then
        assertEquals(HttpStatus.REDIRECT_FOUND.getCode(), res.getStatusCode());
        assertEquals("/user/logout_failed", res.getHeader("Location"));
    }
}
