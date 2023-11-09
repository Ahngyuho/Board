package com.projectboard.repository.querydsl;

import com.projectboard.domain.Article;
import com.projectboard.domain.QArticle;
import com.projectboard.domain.QHashtag;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

import java.util.Collection;
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
                .select(article.hashtags.any().hashtagName)
                .fetch();
    }

    @Override
    public Page<Article> findByHashtagNames(Collection<String> hashtagNames, Pageable pageable) {
        QHashtag hashtag = QHashtag.hashtag;
        QArticle article = QArticle.article;

        JPQLQuery<Article> query = from(article)
                .innerJoin(article.hashtags, hashtag)
                .where(hashtag.hashtagName.in(hashtagNames));

        List<Article> articles = getQuerydsl().applyPagination(pageable,query).fetch();

        return new PageImpl<>(articles, pageable, query.fetchCount());
    }
}
