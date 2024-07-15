package codesquad.server.db;

import codesquad.db.Database;
import codesquad.model.Article;

import java.util.concurrent.atomic.AtomicInteger;

public class ArticleRepository {
    private static final AtomicInteger id = new AtomicInteger(1);

    private ArticleRepository() {
    }

    public static void addArticle(Article article) {
        Database.add("articles", id, article);
        id.incrementAndGet();
    }

    public static String getArticle(String title) {
        return (String) Database.get("articles", title).orElse(null);
    }
}
