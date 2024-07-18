package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.Article;
import codesquad.model.Comment;
import codesquad.model.User;
import codesquad.server.db.ArticleRepository;
import codesquad.server.db.CommentRepository;
import codesquad.server.db.SessionRepository;
import codesquad.server.db.UserRepository;
import codesquad.utils.AuthenticationResolver;
import codesquad.utils.ResourceResolver;

import java.util.List;
import java.util.Optional;

public class ViewHandler {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final AuthenticationResolver authenticationResolver;

    public ViewHandler(UserRepository userRepository, SessionRepository sessionRepository, ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.authenticationResolver = new AuthenticationResolver(userRepository, sessionRepository);
    }

    public void getRegistrationPage(Context ctx) {
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody("<html><h1>registration</h1></html>".getBytes());

    }

    public void getIndexPage(Context ctx) throws RuntimeException {
        int now;
        Optional<String> page = ctx.request().getCookie("page");
        now = page.map(Integer::parseInt).orElse(1);

        ctx.response().addHeader("Set-Cookie", "page=" + now + "; Path=/; HttpOnly");

        Optional<String> sidCookie = ctx.request().getCookie("sid");
        int sid;
        if (sidCookie.isPresent()) { // 쿠키가 있으면 세션 확인
            try { // 쿠키가 숫자가 아닌 경우 예외처리
                sid = Integer.parseInt(sidCookie.get());
            } catch (NumberFormatException e) {
                String template = new String(ResourceResolver.readResourceFileAsBytes("/static/index.html"));
                Article article = articleRepository.getArticle(now);
                if (now != 1 && article == null) {
                    ctx.response()
                            .setStatus(HttpStatus.REDIRECT_FOUND)
                            .addHeader("Location", "/")
                            .addHeader("Set-Cookie", "page=" + 1 + "; Path=/; HttpOnly")
                    ;
                    return;
                }
                if (now == 1 && article == null) {
                    ctx.response()
                            .setStatus(HttpStatus.OK)
                            .addHeader("Content-Type", "text/html")
                            .addHeader("Set-Cookie", "page=" + 1 + "; Path=/; HttpOnly")
                            .setBody(template.replace("{{post}}", "").getBytes());
                    return;
                }

                ctx.response()
                        .addHeader("Content-Type", "text/html")
                        .setBody(template.replace("{{post}}", getArticleHtml(article)).getBytes())
                        .setStatus(HttpStatus.OK)
                ;
                return;
            }
            if (sessionRepository.getSession(sid) != null) {
                String template = new String(ResourceResolver.readResourceFileAsBytes("/static/main/index.html"));
                Optional<User> user = userRepository.getUser(sessionRepository.getSession(sid));
                if (user.isPresent()) {

                    String body = template.replace("{{username}}", user.get().getName());
                    Article article = articleRepository.getArticle(now);
                    if (now != 1 && article == null) {
                        ctx.response()
                                .setStatus(HttpStatus.REDIRECT_FOUND)
                                .addHeader("Location", "/")
                                .addHeader("Set-Cookie", "page=" + 1 + "; Path=/; HttpOnly")
                        ;
                        return;
                    }
                    if (now == 1 && article == null) {
                        ctx.response()
                                .setStatus(HttpStatus.OK)
                                .addHeader("Content-Type", "text/html")
                                .addHeader("Set-Cookie", "page=" + 1 + "; Path=/; HttpOnly")
                                .setBody(body.replace("{{post}}", "").getBytes());
                        return;
                    }
                    body = body.replace("{{post}}", getArticleHtml(article));

                    ctx.response()
                            .addHeader("Content-Type", "text/html")
                            .setStatus(HttpStatus.OK)
                            .setBody(body.getBytes());
                    return;
                }
            }
        }
        String template = new String(ResourceResolver.readResourceFileAsBytes("/static/index.html"));

        Article article = articleRepository.getArticle(now);
        if (article == null) {
            ctx.response()
                    .setStatus(HttpStatus.OK)
                    .setBody(template.replace("{{post}}", "").getBytes())
                    .addHeader("Set-Cookie", "page=" + 1 + "; Path=/; HttpOnly")
            ;
            return;
        }
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody(template.replace("{{post}}", getArticleHtml(article)).getBytes())
                .setStatus(HttpStatus.OK)
        ;
    }

    public void getUserListPage(Context ctx) {
        if (authenticationResolver.isLogin(ctx)) {
            String template = new String(ResourceResolver.readResourceFileAsBytes("/static/user/list/index.html"));
            List<User> users = userRepository.getUsers();

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


    public void getWritePage(Context context) {
        if (authenticationResolver.isLogin(context)) {
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

    private String getArticleHtml(Article article) {
        String defaultPostImg = "class='post__img'";
        String articleTemplate = """
                        <div class="post">
                          <div class="post__account">
                            <img class="post__account__img" />
                            <p class="post__account__nickname">{{username}}</p>
                          </div>
                          <img {post_img} />
                          <div class="post__menu">
                            <ul class="post__menu__personal">
                              <li>
                                <button class="post__menu__btn">
                                  <img src="../img/like.svg" />
                                </button>
                              </li>
                              <li>
                                <button class="post__menu__btn">
                                  <img src="../img/sendLink.svg" />
                                </button>
                              </li>
                            </ul>
                            <button class="post__menu__btn">
                              <img src="../img/bookMark.svg" />
                            </button>
                          </div>
                          <p class="post__article">
                            {{content}}
                          </p>
                        </div>
                        <ul class="comment">
                          {{comments}}
                        </ul>
                        <nav class="nav">
                          <ul class="nav__menu">
                            <li class="nav__menu__item">
                              <a class="nav__menu__item__btn" href="/article/prev">
                                <img
                                  class="nav__menu__item__img"
                                  src="./img/ci_chevron-left.svg"
                                />
                                이전 글
                              </a>
                            </li>
                            <li class="nav__menu__item">
                              <a class="btn btn_ghost btn_size_m" href="/comment">댓글 작성</a>
                            </li>
                            <li class="nav__menu__item">
                              <a class="nav__menu__item__btn" href="/article/next">
                                다음 글
                                <img
                                  class="nav__menu__item__img"
                                  src="./img/ci_chevron-right.svg"
                                />
                              </a>
                            </li>
                          </ul>
                        </nav>
                """;

        List<Comment> comments = commentRepository.getComments(article.getId());
        if (article == null) {
            return "";
        }
        return articleTemplate.replace("{{username}}", article.getUsername())
                .replace("{post_img}", article.getImgSrc() == null ? defaultPostImg : "src=" + article.getImgSrc())
                .replace("{{content}}", article.getContent())
                .replace("{{comments}}", getCommentListHtml(comments))
                ;
    }

    private static String getCommentListHtml(List<Comment> comments) {
        String commentTemplate = """
                <li class="comment__item">
                            <div class="comment__item__user">
                              <img class="comment__item__user__img" />
                              <p class="comment__item__user__nickname">{{username}}</p>
                            </div>
                            <p class="comment__item__article">
                              {{content}}
                            </p>
                          </li>""";
        StringBuilder commentListHtml = new StringBuilder();
        for (var comment : comments) {
            commentListHtml.append(commentTemplate.replace("{{username}}", comment.getUsername())
                    .replace("{{content}}", comment.getContent()));
        }
        return commentListHtml.toString();
    }
}
