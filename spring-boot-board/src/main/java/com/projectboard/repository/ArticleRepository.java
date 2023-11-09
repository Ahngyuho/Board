package com.projectboard.repository;

import com.projectboard.domain.Article;
import com.projectboard.domain.QArticle;
import com.projectboard.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        ArticleRepositoryCustom,
        QuerydslPredicateExecutor<Article>,//Article 검색기능 추가
        QuerydslBinderCustomizer<QArticle>//더 자세한 검색 가능하게
        //
{
    //검색할 때 제목 내용 userId(UserAccount) Nickname(UserAccount)
    //그리고 부분 검색 가능하도록 Containing 붙여줌
    //이것들은 queryDsl 이용해서 동적 쿼리로 만들 수도 있음
    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String title, Pageable pageable);

    //UserId는 UserAccount에 존재함 그래서 이런 식으로 접근
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String title, Pageable pageable);
    //Hashtag 는 카테고리임 완벽히 맞아야 의미가 있다
    Page<Article> findByHashtags(String title, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long articleId, String userId);
    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        //QuerydslPredicateExecutor<Article>
        //이것 때문에 지금 Article 의 모든 필드가 검색에 열려있음
        //하지만 우리는 검색할 때 필요한 것들을 지정해둠
        //선택적으로 검색하게 하고 싶음
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtags, root.createdAt, root.createdBy);
        //id 같은 경우는 인증기능 넣을 때 다시한번 봐보자
        //exatly 한 방식 말고 선택적 검색 하도록
        //first는 검색 파라미터를 하나만 받게 한 것임
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::containsIgnoreCase);
        //얘는 String 이 아님 DateTimeExpressions 라는 것이 있음
        //이건 어쩔 수 없이 full 로 다 써줘야 검색 가능할 듯
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}