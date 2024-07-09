package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.http.Server;
import codesquad.model.User;
import codesquad.utils.JsonConverter;

public class UserHandler {
    private UserHandler() {
    }

    public static void createUser(Context ctx) {
        User user = new User(ctx.request().getQuery("userId"), ctx.request().getQuery("password"), ctx.request().getQuery("name"), ctx.request().getQuery("email"));
        var json = JsonConverter.toJson(user).getBytes();
        Server.addUser(user);
        ctx.response().setStatus(HttpStatus.REDIRECT_FOUND)
                .addHeader("Location", "/")
                .addHeader("Content-Type", "application/json")
                .setBody(json);
    }

    public static void login(Context ctx) {
        User user;
        int sid = -1;
        try {
            user = Server.getUser(ctx.request().getQuery("userId")).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            sid = Server.addSession(user.getUserId());
        } catch (IllegalArgumentException e) {
            ctx.response()
                    .setStatus(HttpStatus.NO_CONTENT)
                    .setBody(e.getMessage().getBytes());
            return;
        }
        if (user.checkPassword(ctx.request().getQuery("password"))) {
            ctx.response()
                    .setStatus(HttpStatus.OK)
                    .addHeader("set-cookie", String.format("sid=%d; Path=/", sid));
        } else {
            ctx.response().setStatus(HttpStatus.UNAUTHORIZED);
        }
    }
}
