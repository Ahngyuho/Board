package com.projectboard.service;

import com.projectboard.domain.Article;
import com.projectboard.domain.UserAccount;
import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.ArticleWithCommentsDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    //sut 는 테스트의 대상임을 의미
    //Mock 을 주입하는 대상을 이렇게 지정
    @InjectMocks private ArticleService sut;
    //나머지는 Mock 이라고 줌
    @Mock private ArticleRepository articleRepository;


    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticleList() {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        BDDMockito.given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        //When
        //검색어와 검색 타입만 받게 만들자 type 은 enum?
        //searchArticles 는 이런 입력 파라미터를 받아서 ArticleDto list를 반환
        Page<ArticleDto> articles = sut.searchArticles(null,null,pageable);   //제목,본몬,id,넥네임,해시태그
        //Then
        assertThat(articles).isEmpty();
        BDDMockito.then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다")
    @Test
    void givenSearchParameters_whenSearchingArticle_thenReturnsArticle() {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        BDDMockito.given(articleRepository.findByTitleContaining(searchKeyword,pageable)).willReturn(Page.empty());
        //When
        Page<ArticleDto> articles = sut.searchArticles(searchType,searchKeyword,pageable);
        //Then
        assertThat(articles).isEmpty();
        BDDMockito.then(articleRepository).should().findByTitleContaining(searchKeyword,pageable);
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSavingArticle_thenSavesArticle() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //When
        //에러 나면 예외 던져주기
        ArticleWithCommentsDto dto = sut.getArticle(articleId);
        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSavingArticle_thenThrowsException() {
        //Given
        Long articleId = 0L;
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //When
        Throwable t = catchThrowable(()->sut.getArticle(articleId));
        //Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSaveArticle() {
        //Given
        ArticleDto dto = createArticleDto();
        //이게 저 saveArticle 에 있어야 한다는 거다
        BDDMockito.given(articleRepository.save(ArgumentMatchers.any(Article.class))).willReturn(createArticle());

        //When
        sut.saveArticle(dto);

        //Then
        BDDMockito.then(articleRepository).should().save(ArgumentMatchers.any(Article.class));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        //Article.class 를 넣어주면 리턴으로 Article.class 가 리턴된다
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀","새 내용","#springboot");
        //getReferenceById 는 findById 와 유사한데 findById 는 쿼리를 날림
        BDDMockito.given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //When
        //에러 나면 예외 던져주기
        //dto 를 update 전용으로 만들어주자
        sut.updateArticle(dto);

        //Then
        //검사
        //save 를 한번 호출했는지 검사
        //저 sut.saveArticle 을 호출하고 articleRepository 의 save 가 호출 됐는지 확인
        //이런 식으로 mocking 을 이용해서 test 가능
        //근데 만약 데이터 베이스에 실제로 저장이 됐는지 확인하고 싶으면 mock 을 쓰면 안된다
        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        BDDMockito.then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        BDDMockito.given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto);

        // Then
        BDDMockito.then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        BDDMockito.willDoNothing().given(articleRepository).deleteById(articleId);

        // When
        sut.deleteArticle(1L);

        // Then
        BDDMockito.then(articleRepository).should().deleteById(articleId);
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "uno",
                "password",
                "uno@email.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}