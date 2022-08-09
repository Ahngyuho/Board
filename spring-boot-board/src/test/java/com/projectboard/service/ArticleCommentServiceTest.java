package com.projectboard.service;

import com.projectboard.config.SecurityConfig;
import com.projectboard.domain.Article;
import com.projectboard.domain.ArticleComment;
import com.projectboard.domain.UserAccount;
import com.projectboard.dto.ArticleCommentDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.repository.ArticleCommentRepository;
import com.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Disabled
@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    @InjectMocks private ArticleCommentService sut;

    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글 id 로 조회하면 해당하는 댓글 list 반환")
    @Test
    void givenArticleId_whenSearchingComments_thenReturnsComments() {
        //Given
        Long articleId = 1L;

//        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.of(
//                Article.of("title", "content", "#java")));
        ArticleComment expected = createArticleComment("content");
        //findByArticle_Id 생성 해줘야 한다
        BDDMockito.given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));

        //When
        //댓글도 dto 로
        List<ArticleCommentDto> actual = sut.searchArticleComments(articleId);

        //Then
//        assertThat(articleComments).isNotNull();
//        BDDMockito.then(articleRepository).should().findById(articleId);
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content",expected.getContent());
        BDDMockito.then(articleCommentRepository).should().findByArticle_Id(articleId);
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(Article.of(createUserAccount(),"title","content","hashtag"),
                createUserAccount(),
                content
        );
    }

    @DisplayName("댓글 정보를 입력하면 , 댓글 저장한다.")
    @Test
    void givenArticleInfo_whenSearchingComments_thenReturnsComments() {
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        BDDMockito.given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        BDDMockito.given(articleCommentRepository.save(ArgumentMatchers.any(ArticleComment.class))).willReturn(null);

        //When
        //댓글도 dto 로
        sut.saveArticleComment(dto);

        //Then
        BDDMockito.then(articleRepository).should().getReferenceById(dto.articleId());
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안 한다.")
    @Test
    void givenNonexistentArticle_whenSavingArticleComment_thenLogsSituationAndDoesNothing() {
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        BDDMockito.given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);
        //When
        sut.saveArticleComment(dto);
        //Then
        BDDMockito.then(articleRepository).should().getReferenceById(dto.articleId());
        BDDMockito.then(articleCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
    @Test
    void givenArticleCommentInfo_whenUpdatingArticleComment_thenUpdatesArticleComment() {
        //Given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
        BDDMockito.given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);
        //When
        sut.updateArticleComment(dto);

        //Then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        BDDMockito.then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무 것도 안 한다.")
    @Test
    void givenNonexistentArticleComment_whenUpdatingArticleComment_thenLogsWarningAndDoesNothing() {
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        BDDMockito.given(articleCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);
        //When
        sut.updateArticleComment(dto);
        //Then
        BDDMockito.then(articleCommentRepository).should().getReferenceById(dto.id());
    }
    
    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    @Test
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment() {
        //Given
        Long articleCommentId = 1L;
        BDDMockito.willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);
        //When
        sut.deleteArticleComment(articleCommentId);

        //Then
        BDDMockito.then(articleCommentRepository).should().deleteById(articleCommentId);
    }

    //이건 뭐냐면 테스트 용 데이터 세팅임
    //이런 방식을 fixture 라고 함 테스트 코드 내내 등장
    //근데 원래 따로 파일을 만들어서 작성하는데 리팩토링 여지를 남겨두고자 이렇게 함
    private ArticleCommentDto createArticleCommentDto(String content){
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
                LocalDateTime.now(),
                "agh",
                LocalDateTime.now(),
                "agh"
        );
    }
    private UserAccountDto createUserAccountDto(){
        return UserAccountDto.of(
                1L,
                "agh",
                "password",
                "agh@gmail.com",
                "agh",
                "This is memo",
                LocalDateTime.now(),
                "agh",
                LocalDateTime.now(),
                "agh"
        );
    }
    private UserAccount createUserAccount() {
        return UserAccount.of(
                "agh",
                "password",
                "agh@gmail.com",
                "agh",
                null
        );
    }

    private Article createArticle(){
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }




}