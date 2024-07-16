package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.Article;
import codesquad.model.User;
import codesquad.server.db.ArticleRepository;
import codesquad.server.db.CommentRepository;

import java.util.Optional;

import static codesquad.utils.AuthenticationResolver.getUserDetail;
import static codesquad.utils.AuthenticationResolver.isLogin;

public class ArticleHandler {
    private ArticleHandler() {
    }

    public static void write(Context ctx) {
        if (isLogin(ctx)) {
            User user = (User) getUserDetail(ctx);
            ArticleRepository.addArticle(new Article(user.getUserId(), user.getName(), ctx.request().getQuery("content")));
            ctx.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Content-Type", "text/html")
                    .addHeader("Location", "/");
        } else {
            ctx.response()
                    .setStatus(HttpStatus.UNAUTHORIZED)
                    .addHeader("Content-Type", "text/html")
                    .setBody("Unauthorized".getBytes());
        }
    }

    public static void addComment(Context ctx) {
        if (isLogin(ctx)) {

            User user = (User) getUserDetail(ctx);
            Optional<String> page = ctx.request().getCookie("page");
            if (page.isPresent()) {
                CommentRepository.addComment(user, Long.parseLong(page.get()), ctx.request().getQuery("content"));

                ctx.response()
                        .setStatus(HttpStatus.REDIRECT_FOUND)
                        .addHeader("Content-Type", "text/html")
                        .addHeader("Location", "/");
            } else {
                ctx.response()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .addHeader("Content-Type", "text/html")
                        .setBody("Bad Request".getBytes());

            }
        } else {
            ctx.response()
                    .setStatus(HttpStatus.UNAUTHORIZED)
                    .addHeader("Content-Type", "text/html")
                    .setBody("Unauthorized".getBytes());
        }
    }
}
