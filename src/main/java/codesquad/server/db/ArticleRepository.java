package codesquad.server.db;

import codesquad.model.Article;

public interface ArticleRepository {
    void addArticle(Article article);

    Article getArticle(long id);
}
