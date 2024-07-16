package codesquad.server.db;

import codesquad.db.Database;
import codesquad.model.Article;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ArticleRepository {
    private static final AtomicLong id = new AtomicLong(1);
    private static final String DBNAME = "articles";
    private ArticleRepository() {
    }

    public static void addArticle(Article article) {
        article.setId(id.get());
        Database.save(DBNAME, id.get(), article);
        id.incrementAndGet();
    }

    public static Article getArticle(long id) {
        return (Article) Database.get(DBNAME, id).orElse(null);
    }

    public static void update(Article article) {
        Database.save(DBNAME, article.getId(), article);

    }

    public static List<Article> getArticles() {
        return Database.getList(DBNAME).stream()
                .filter(Article.class::isInstance)
                .map(Article.class::cast)
                .toList();
    }
}
