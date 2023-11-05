package com.projectboard.repository;

import com.projectboard.config.JpaConfig;
import com.projectboard.domain.Article;
import com.projectboard.domain.UserAccount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JPA 연결 테스트")
//슬라이스 테스트
@DataJpaTest
//이건 내가 만든 config 를 이 테스트에게 알려주기 위함임
//@Import(JpaConfig.class) 여기서는 JpaConfig 를 해주면 안된다.
    //JpaConfig 를 보면 지금 Spring Security 를 사용하고 있다.
    //그래서 이 슬라이스 테스트에서는 동작할 수 없다 그래서 아래의 해당 테스트 객체 전용 Jpa Auditor 를 만들어 준것이다.
@Import(JpaRepositoryTest.TestJpaConfig.class)
public
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


    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig{
        //이건 Spring Security 사용으로인한 Jpa Auditor 설정 변경으로
        //위의 test 가 제대로 동작하지 않는 즉 Jpa Auditor 가
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("agh");
        }
    }
}