package com.projectboard.service;

import com.projectboard.domain.Article;
import com.projectboard.domain.UserAccount;
import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.repository.ArticleRepository;
import com.projectboard.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    //sut 는 테스트의 대상임을 의미
    //Mock 을 주입하는 대상을 이렇게 지정
    @InjectMocks private ArticleService sut;
    //나머지는 Mock 이라고 줌
    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;


    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticleList() throws IllegalAccessException {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        //When
        //검색어와 검색 타입만 받게 만들자 type 은 enum?
        //searchArticles 는 이런 입력 파라미터를 받아서 ArticleDto list를 반환
        Page<ArticleDto> articles = sut.searchArticles(null,null,pageable);   //제목,본몬,id,넥네임,해시태그
        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다")
    @Test
    void givenSearchParameters_whenSearchingArticle_thenReturnsArticle() throws IllegalAccessException {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword,pageable)).willReturn(Page.empty());
        //When
        Page<ArticleDto> articles = sut.searchArticles(searchType,searchKeyword,pageable);
        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword,pageable);
    }

    @DisplayName("검색어 없이 게시글을 해시태그 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticleViaHashtag_thenReturnsEmptyPage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null,pageable);
        //Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("검색어 없이 게시글을 해시태그 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticleViaHashtag_thenReturnsPage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        String hashtag = "#java";
        //empty 를 반환하는 이유는 과정이 더 중요하기 때문, 어떤 결과가 나오는지는 사실 moking 된 데이터기 때문에 전혀 중요하지 않음
        //내가 뭘 넣는지 여기서 정하는 거기 때문에 결과도 내가 넣어준 데이터가 나와야 할 것이 자명함.
        given(articleRepository.findByHashtag(hashtag,pageable)).willReturn(Page.empty(pageable));
        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag,pageable);
        //Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtag(hashtag,pageable);
    }

    @DisplayName("게시글 수를 조회하면,게시글 수를 반환한다.")
    @Test
    void givenNothing_whenCountingAriticles_thenReturnsArticleCount() {
        //given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        //when
        long actual = sut.getArticleCount();

        //then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    @DisplayName("해시태그를 검색하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    //아무것도 주는것(given) 없고 호출하면 결과줘라.
    void givenNothing_whenCalling_thenReturnsHashtags() {
        //Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);
        //When
        List<String> actualHashtags = sut.getHashtags();
        //Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }



    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSavingArticle_thenSavesArticle() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //When
        //에러 나면 예외 던져주기
        ArticleDto dto = sut.getArticle(articleId);
        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSavingArticle_thenThrowsException() {
        //Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //When
        Throwable t = catchThrowable(()->sut.getArticle(articleId));
        //Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSaveArticle() {
        //Given
        ArticleDto dto = createArticleDto();
        //이게 저 saveArticle 에 있어야 한다는 거다
        given(articleRepository.save(ArgumentMatchers.any(Article.class))).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId())).willReturn(createUserAccount());

        //When
        sut.saveArticle(dto);

        //Then
        then(userAccountRepository).should().getReferenceById(dto.getUserAccountDto().getUserId());
        then(articleRepository).should().save(ArgumentMatchers.any(Article.class));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        //Article.class 를 넣어주면 리턴으로 Article.class 가 리턴된다
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀","새 내용","#springboot");
        //getReferenceById 는 findById 와 유사한데 findById 는 쿼리를 날림
        given(articleRepository.getReferenceById(dto.getId())).willReturn(article);

        //When
        //에러 나면 예외 던져주기
        //dto 를 update 전용으로 만들어주자
        sut.updateArticle(dto.getId(),dto);

        //Then
        //검사
        //save 를 한번 호출했는지 검사
        //저 sut.saveArticle 을 호출하고 articleRepository 의 save 가 호출 됐는지 확인
        //이런 식으로 mocking 을 이용해서 test 가능
        //근데 만약 데이터 베이스에 실제로 저장이 됐는지 확인하고 싶으면 mock 을 쓰면 안된다
        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.getTitle())
                .hasFieldOrPropertyWithValue("content", dto.getContent())
                .hasFieldOrPropertyWithValue("hashtag", dto.getHashtag());
        then(articleRepository).should().getReferenceById(dto.getId());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.getId())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto.getId(),dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.getId());
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        String userId = "aghTest";
        willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId,userId);

        // When
        sut.deleteArticle(1L,userId);

        // Then
        then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId,userId);
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