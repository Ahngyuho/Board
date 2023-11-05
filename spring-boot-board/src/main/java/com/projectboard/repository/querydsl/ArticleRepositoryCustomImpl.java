package com.projectboard.repository.querydsl;

import com.projectboard.domain.Article;
import com.projectboard.domain.QArticle;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;
        return from(article)
                .distinct()
                //그냥 article 해주면 그냥 Spring Data Jpa 쓰면 된다.
                //지금 난 특정 컬럼의 값들을 list 형태로 만들어 주기위해 querydsl 을 사용하는 것이다.
                .select(article.hashtag)
                .where(article.hashtag.isNotNull())
                .fetch();
    }

}
