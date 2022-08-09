package com.projectboard.repository;

import com.projectboard.domain.ArticleComment;
import com.projectboard.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment,Long>,
        QuerydslPredicateExecutor<ArticleComment>,
        QuerydslBinderCustomizer<QArticleComment> {
    //왜 Article_id 라고 했을까
    //이건 댓글 id로 검색을 하는 것이 아니라 게시글로 댓글을 검색하는 것임
    //이렇게 _id 해주면 Article 의 Id 로 들어가게 됨
    List<ArticleComment> findByArticle_Id(Long articleId);
    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root){
        //QuerydslPredicateExecutor<Article>
        //이것 때문에 지금 Article 의 모든 필드가 검색에 열려있음
        //하지만 우리는 검색할 때 필요한 것들을 지정해둠
        //선택적으로 검색하게 하고 싶음
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content,root.createdAt,root.createdBy);

        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        //얘는 String 이 아님 DateTimeExpressions 라는 것이 있음
        //이건 어쩔 수 없이 full 로 다 써줘야 검색 가능할 듯
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
