package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.server.db.SessionRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ViewRegistrationHandler {
    private ViewRegistrationHandler() {
    }

    public static void viewRegistration(Context ctx) {
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody("<html><h1>registration</h1></html>".getBytes());

    }

    public static void getIndexPage(Context ctx) {
        Optional<String> cookie = ctx.request().getHeader("Cookie");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get().split("=")[1]);
            if (SessionRepository.getSession(sid) != null) {
                ctx.response()
                        .addHeader("Content-Type", "text/html")
                        .setStatus(HttpStatus.OK)
                        .setBody(readResourceFileAsBytes("/static/main/index.html"));
                return;
            }
        }
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody(readResourceFileAsBytes("/static/index.html"))
                .setStatus(HttpStatus.OK)
        ;
    }

    private static byte[] readResourceFileAsBytes(String resourcePath) {
        try (InputStream is = ViewRegistrationHandler.class.getResourceAsStream(resourcePath);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
