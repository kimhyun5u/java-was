package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.User;
import codesquad.server.db.SessionRepository;
import codesquad.server.db.UserRepository;
import codesquad.utils.ResourceResolver;

import java.util.List;
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
        int sid;
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            try { // 쿠키가 숫자가 아닌 경우 예외처리
                sid = Integer.parseInt(cookie.get().split("=")[1]);
            } catch (NumberFormatException e) {
                ctx.response()
                        .addHeader("Content-Type", "text/html")
                        .setBody(ResourceResolver.readResourceFileAsBytes("/static/index.html"))
                        .setStatus(HttpStatus.OK)
                ;
                return;
            }
            if (SessionRepository.getSession(sid) != null) {
                String template = new String(ResourceResolver.readResourceFileAsBytes("/static/main/index.html"));
                Optional<User> user = UserRepository.getUser(SessionRepository.getSession(sid));
                if (user.isPresent()) {

                    String body = template.replace("{{username}}", user.get().getName());
                    ctx.response()
                            .addHeader("Content-Type", "text/html")
                            .setStatus(HttpStatus.OK)
                            .setBody(body.getBytes());
                    return;
                }
            }
        }
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody(ResourceResolver.readResourceFileAsBytes("/static/index.html"))
                .setStatus(HttpStatus.OK)
        ;
    }

    public static void getUserListPage(Context ctx) {
        if (isLogin(ctx)) {
            String template = new String(ResourceResolver.readResourceFileAsBytes("/static/user/list/index.html"));
            List<User> users = UserRepository.getUsers();

            StringBuilder userListHtml = new StringBuilder();
            for (User user : users) {
                userListHtml.append("<tr>")
                        .append("<td>").append(user.getUserId()).append("</td>")
                        .append("<td>").append(user.getName()).append("</td>")
                        .append("<td>").append(user.getEmail()).append("</td>")
                        .append("</tr>");
            }

            String body = template.replace("{{userList}}", userListHtml.toString());

            ctx.response()
                    .setStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "text/html")
                    .setBody(body.getBytes());
            return;
        }

        ctx.response()
                .setStatus(HttpStatus.REDIRECT_FOUND)
                .addHeader("Location", "/login");

    }


    public static void getWritePage(Context context) {
        if (isLogin(context)) {
            context.response()
                    .setStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "text/html")
                    .setBody(ResourceResolver.readResourceFileAsBytes("/static/article/index.html"));
            return;
        }

        context.response()
                .setStatus(HttpStatus.REDIRECT_FOUND)
                .addHeader("Location", "/login");
    }

    private static boolean isLogin(Context ctx) {
        Optional<String> cookie = ctx.request().getHeader("Cookie");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get().split("=")[1]);
            return SessionRepository.isValid(sid);
        }

        return false;
    }
}
