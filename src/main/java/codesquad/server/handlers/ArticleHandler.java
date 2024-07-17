package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.Article;
import codesquad.model.User;
import codesquad.server.db.ArticleRepository;
import codesquad.server.db.CommentRepository;
import codesquad.utils.AuthenticationResolver;

import java.util.Optional;

public class ArticleHandler {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final AuthenticationResolver authenticationResolver;

    public ArticleHandler(ArticleRepository articleRepository, CommentRepository commentRepository, AuthenticationResolver authenticationResolver) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.authenticationResolver = authenticationResolver;
    }

    public void write(Context ctx) {
        if (authenticationResolver.isLogin(ctx)) {
            User user = (User) authenticationResolver.getUserDetail(ctx);
            articleRepository.addArticle(new Article(user.getUserId(), user.getName(), ctx.request().getQuery("content")));
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

    public void addComment(Context ctx) {
        if (authenticationResolver.isLogin(ctx)) {

            User user = (User) authenticationResolver.getUserDetail(ctx);
            Optional<String> page = ctx.request().getCookie("page");
            if (page.isPresent()) {
                commentRepository.addComment(user, Long.parseLong(page.get()), ctx.request().getQuery("content"));

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

    public void prevArticle(Context context) {
        Optional<String> page = context.request().getCookie("page");
        if (page.isPresent()) {
            long prevPage = Long.parseLong(page.get()) - 1;
            if (prevPage < 1) {
                prevPage = 1;
            }
            context.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Location", "/")
                    .addHeader("Set-Cookie", "page=" + prevPage + "; Path=/; HttpOnly;");
        } else {
            context.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Location", "/")
                    .addHeader("set-cookie", "page=" + 1 + "; Path=/; HttpOnly;");
        }
    }

    public void nextArticle(Context context) {
        Optional<String> page = context.request().getCookie("page");
        if (page.isPresent()) {
            long nextPage = Long.parseLong(page.get()) + 1;
            context.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Location", "/")
                    .addHeader("set-cookie", "page=" + nextPage + "; Path=/; HttpOnly;");
        } else {
            context.response()
                    .setStatus(HttpStatus.REDIRECT_FOUND)
                    .addHeader("Location", "/")
                    .addHeader("Set-Cookie", "page=" + 1 + "; Path=/; HttpOnly;");
        }
    }
}
