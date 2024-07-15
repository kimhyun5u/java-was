package codesquad.utils;

import codesquad.http.Context;
import codesquad.server.db.SessionRepository;
import codesquad.server.db.UserRepository;

import java.util.Optional;

public class AuthenticationResolver {
    private AuthenticationResolver() {
    }

    public static boolean isLogin(Context ctx) {
        Optional<String> cookie = ctx.request().getHeader("Cookie");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get().split("=")[1]);
            return SessionRepository.isValid(sid);
        }

        return false;
    }

    public static Object getUserDetail(Context ctx) {
        String username = null;
        Optional<String> cookie = ctx.request().getHeader("Cookie");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get().split("=")[1]);
            if (SessionRepository.isValid(sid)) { // 유효한 세션인지 확인
                username = SessionRepository.getSession(sid);

            }
        }
        return UserRepository.getUser(username).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
