package com.projectboard.repository;

import com.projectboard.domain.Article;
import com.projectboard.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>,//Article 검색기능 추가
        QuerydslBinderCustomizer<QArticle>//더 자세한 검색 가능하게
{
    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        //QuerydslPredicateExecutor<Article>
        //이것 때문에 지금 Article 의 모든 필드가 검색에 열려있음
        //하지만 우리는 검색할 때 필요한 것들을 지정해둠
        //선택적으로 검색하게 하고 싶음
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title,root.content,root.hashtag,root.createdAt,root.createdBy);
        //id 같은 경우는 인증기능 넣을 때 다시한번 봐보자
        //exatly 한 방식 말고 선택적 검색 하도록
        //first는 검색 파라미터를 하나만 받게 한 것임
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  //like '%${v}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        //얘는 String 이 아님 DateTimeExpressions 라는 것이 있음
        //이건 어쩔 수 없이 full 로 다 써줘야 검색 가능할 듯
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}