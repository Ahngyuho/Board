package com.projectboard.repository;

import com.projectboard.config.JpaConfig;
import com.projectboard.domain.Article;
import com.projectboard.domain.UserAccount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


import static org.assertj.core.api.Assertions.*;

@Disabled
@DisplayName("JPA 연결 테스트")
//슬라이스 테스트
@DataJpaTest
//이건 내가 만든 config 를 이 테스트에게 알려주기 위함임
@Import(JpaConfig.class)
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository,
                             @Autowired UserAccountRepository userAccountRepository){
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        //Given
        long previousCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("uno","pw",null,null,null));
        Article article = Article.of(userAccount,"new article","new content","#spring");

        //When
        articleRepository.save(article);
        //Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        //Given
        long previousCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("uno", "pw", null, null, null));
        Article article = Article.of(userAccount, "new article", "new content", "#spring");
        //When
        articleRepository.save(article);

        //Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    /**
     * update 테스트
     */
    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();//없다면 throw 해서 테스트 종료
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);
        //When
        Article savedArticle = articleRepository.saveAndFlush(article); //지금 롤백이 되는 상황이라 변경돼도
        // 쿼리가 안 날아감 그래서 이 saveAndFlush 사용 flush 하게 하는것임 이렇게 해야 update 쿼리가 생김

        //Then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag",updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();//없다면 throw 해서 테스트 종료
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();    //이건 여러개 일 수 있기 때문에 한개로 지정하지 않음
        int deletedCommentsSize = article.getArticleComments().size();

        //When
        articleRepository.delete(article); //지금 롤백이 되는 상황이라 변경돼도
        // 쿼리가 안 날아감 그래서 이 saveAndFlush 사용 flush 하게 하는것임 이렇게 해야 update 쿼리가 생김

        //Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

}