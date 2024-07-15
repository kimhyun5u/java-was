package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.Article;
import codesquad.model.User;
import codesquad.server.db.ArticleRepository;

import static codesquad.utils.AuthenticationResolver.getUserDetail;
import static codesquad.utils.AuthenticationResolver.isLogin;

public class ArticleHandler {
    private ArticleHandler() {
    }

    public static void write(Context ctx) {
        if (isLogin(ctx)) {
            User user = (User) getUserDetail(ctx);
            ArticleRepository.addArticle(new Article(user, ctx.request().getQuery("content")));
            ctx.response()
                    .setStatus(HttpStatus.CREATED)
                    .addHeader("Content-Type", "text/html")
                    .setBody("Write".getBytes());
        } else {
            ctx.response()
                    .setStatus(HttpStatus.UNAUTHORIZED)
                    .addHeader("Content-Type", "text/html")
                    .setBody("Unauthorized".getBytes());
        }
    }
}
