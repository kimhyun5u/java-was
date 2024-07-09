package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.server.db.SessionRepository;
import codesquad.utils.ResourceResolver;

import java.util.Optional;

public class ViewHandler {
    private ViewHandler() {
    }

    public static void getRegistrationPage(Context ctx) {
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
                        .setBody(ResourceResolver.readResourceFileAsBytes("/static/main/index.html"));
                return;
            }
        }
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody(ResourceResolver.readResourceFileAsBytes("/static/index.html"))
                .setStatus(HttpStatus.OK)
        ;
    }
}
