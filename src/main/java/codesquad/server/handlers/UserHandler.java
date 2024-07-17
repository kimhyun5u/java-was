package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.User;
import codesquad.server.db.SessionRepository;
import codesquad.server.db.UserRepository;
import codesquad.utils.JsonConverter;

import java.util.Optional;

public class UserHandler {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UserHandler(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public void createUser(Context ctx) {
        User user = new User(ctx.request().getQuery("userId"), ctx.request().getQuery("password"), ctx.request().getQuery("name"), ctx.request().getQuery("email"));
        var json = JsonConverter.toJson(user).getBytes();
        userRepository.addUser(user);
        ctx.response().setStatus(HttpStatus.REDIRECT_FOUND)
                .addHeader("Location", "/")
                .addHeader("Content-Type", "application/json")
                .setBody(json);
    }

    public void login(Context ctx) {
        User user;
        int sid = -1;

        try {
            user = userRepository.getUser(ctx.request().getQuery("userId")).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            sid = sessionRepository.addSession(user.getUserId());
        } catch (IllegalArgumentException e) {
            ctx.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Location", "/user/login_failed")
                    .addHeader("Content-Type", "text/html")
                    .setBody(e.getMessage().getBytes());
            return;
        }
        if (user.checkPassword(ctx.request().getQuery("password"))) {
            ctx.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Location", "/")
                    .addHeader("set-cookie", String.format("sid=%d; Path=/; Max-Age=86400; HttpOnly;", sid));
        } else {
            ctx.response().setStatus(HttpStatus.UNAUTHORIZED);
        }
    }

    public void logout(Context ctx) {
        Optional<String> cookie = ctx.request().getCookie("sid");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get());
            if (sessionRepository.isValid(sid)) { // 유효한 세션인지 확인
                sessionRepository.removeSession(sid);

                ctx.response()
                        .setStatus(HttpStatus.REDIRECT_FOUND)
                        .addHeader("Location", "/")
                        .addHeader("set-cookie", "sid=; Path=/; Max-Age=0;");

                return;
            }
        }

        ctx.response()
                .setStatus(HttpStatus.REDIRECT_FOUND)
                .addHeader("Location", "/user/logout_failed")
                .addHeader("Content-Type", "text/html")
                .setBody("Logout failed".getBytes());
    }
}
