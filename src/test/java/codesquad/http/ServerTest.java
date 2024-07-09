package codesquad.http;

import codesquad.model.User;
import codesquad.server.handlers.UserHandler;
import codesquad.utils.JsonConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerTest {

    private static final int THREAD_POOL_SIZE = 10;
    private int port = 9000;
    private Server server;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {// 사용 가능한 랜덤 포트 찾기

        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server = Server.defaultServer(port, THREAD_POOL_SIZE);
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        // ExecutorService 종료
        executorService.shutdownNow();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("서버 시작 및 요청 처리")
    void testServerStartAndHandleRequest() throws IOException, InterruptedException {
        // 테스트용 핸들러 등록
        server.get("/test", ctx -> {
            ctx.response()
                    .setStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "text/plain")
                    .setBody("Test response".getBytes());
        });

        // 별도의 스레드에서 서버 시작
        executorService.submit(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // HTTP 요청 보내기
        URL url = new URL("http://localhost:" + port + "/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 응답 확인
        int responseCode = connection.getResponseCode();
        assertEquals(HttpStatus.OK.getCode(), responseCode);


        // 연결 종료
        connection.disconnect();
    }

    @Test
    @DisplayName("요청 경로 없음")
    void testNotFoundRoute() throws IOException, InterruptedException {
        executorService.submit(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        URL url = new URL("http://localhost:" + port + "/nonexistent");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(HttpStatus.NOT_FOUND.getCode(), responseCode);

        connection.disconnect();
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 메소드 요청")
    void testCreateUserFailure() throws IOException {
        server.post("/create", UserHandler::createUser);

        executorService.submit(() ->
        {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        URL url = new URL("http://localhost:" + port + "/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Host", "localhost:8080");
        conn.setRequestProperty("Connection", "keep-alive");
        assertEquals(HttpStatus.NOT_FOUND.getCode(), conn.getResponseCode());
    }

    @Test
    @DisplayName("회원가입 성공")
    void testCreateUser() throws IOException {
        byte[] expect = JsonConverter.toJson(new User("javajigi1", "password1", "박재성", "javajigi@slipp.net")).getBytes();
        server.staticFiles("/", "/static");
        server.post("/create", UserHandler::createUser);

        executorService.submit(() ->
        {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        URL url = new URL("http://localhost:" + port + "/create");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "localhost:8080");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Length", "95");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "*/*");
        conn.setDoOutput(true);
        // redirect 방지
        conn.setInstanceFollowRedirects(false);
        String formData = "userId=javajigi1&password=password1&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        // 데이터 쓰기
        try (BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
            os.write(formData.getBytes());
            os.flush();
        }

        assertEquals("POST", conn.getRequestMethod());
        assertEquals(HttpStatus.REDIRECT_FOUND.getCode(), conn.getResponseCode());
        assertArrayEquals(expect, conn.getInputStream().readAllBytes());
    }
}
