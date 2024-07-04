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
}
