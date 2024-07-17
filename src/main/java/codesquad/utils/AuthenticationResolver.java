package codesquad.utils;

import codesquad.http.Context;
import codesquad.server.db.SessionRepository;
import codesquad.server.db.UserRepository;

import java.util.Optional;

public class AuthenticationResolver {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthenticationResolver(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public boolean isLogin(Context ctx) {
        Optional<String> cookie = ctx.request().getCookie("sid");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get());
            return sessionRepository.isValid(sid);
        }

        return false;
    }

    public Object getUserDetail(Context ctx) {
        String username = null;
        Optional<String> cookie = ctx.request().getCookie("sid");
        if (cookie.isPresent()) { // 쿠키가 있으면 세션 확인
            int sid = Integer.parseInt(cookie.get());
            if (sessionRepository.isValid(sid)) { // 유효한 세션인지 확인
                username = sessionRepository.getSession(sid);

            }
        }
        return userRepository.getUser(username).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
