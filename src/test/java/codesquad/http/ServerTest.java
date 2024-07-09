package codesquad.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerTest {

    private static final int PORT = 9000;
    private static final int THREAD_POOL_SIZE = 10;
    private Server server;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        server = new Server(PORT, THREAD_POOL_SIZE);
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executorService.shutdownNow();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
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

        // 서버가 시작될 때까지 잠시 대기
        Thread.sleep(1000);

        // HTTP 요청 보내기
        URL url = new URL("http://localhost:" + PORT + "/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 응답 확인
        int responseCode = connection.getResponseCode();
        assertEquals(HttpStatus.OK.getCode(), responseCode);


        // 연결 종료
        connection.disconnect();
    }

    @Test
    void testNotFoundRoute() throws IOException, InterruptedException {
        executorService.submit(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(1000);

        URL url = new URL("http://localhost:" + PORT + "/nonexistent");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(HttpStatus.BAD_REQUEST.getCode(), responseCode);

        connection.disconnect();
    }

    @Test
    void testCreateUserFailure() throws IOException {
        server.post("/create", CreateUserHandler::createUser);

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
    void testCreateUser() throws IOException {
        server.post("/create", CreateUserHandler::createUser);

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
        conn.setRequestProperty("Content-Length", "59");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "*/*");
        conn.setDoOutput(true);
        String formData = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        // 데이터 쓰기
        try (BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
            os.write(formData.getBytes());
            os.flush();
        }

        assertEquals(HttpStatus.CREATED.getCode(), conn.getResponseCode());
    }
}
