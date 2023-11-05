package com.projectboard.service;

import com.projectboard.domain.Article;
import com.projectboard.domain.ArticleComment;
import com.projectboard.domain.UserAccount;
import com.projectboard.dto.ArticleCommentDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.repository.ArticleCommentRepository;
import com.projectboard.repository.ArticleRepository;
import com.projectboard.repository.UserAccountRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Disabled
@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    //파라미터 주입 X 그래서 필드 주입으로 해준것
    @InjectMocks private ArticleCommentService sut;

    //Mock 은 필드, 파라미터 주입 가능 하지만 위의 InjectMocks 가 파라미터 주입 지원 X
    //그래서 그냥 모든 프로퍼티? 필드 주입으로 해줌
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("게시글 id 로 조회하면 해당하는 댓글 list 반환")
    @Test
    void givenArticleId_whenSearchingComments_thenReturnsComments() {
        //Given
        Long articleId = 1L;
        ArticleCommentDto dto = createArticleCommentDto("댓글");

//        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.of(
//                Article.of("title", "content", "#java")));
        ArticleComment expected = createArticleComment("content");
        //findByArticle_Id 생성 해줘야 한다
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));
//        given(userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId())).willReturn(createUserAccount());

        //When
        //댓글도 dto 로
        List<ArticleCommentDto> actual = sut.searchArticleComments(articleId);

        //Then
//        assertThat(articleComments).isNotNull();
//        BDDMockito.then(articleRepository).should().findById(articleId);
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content",expected.getContent());
//        then(userAccountRepository).should().getReferenceById(dto.getUserAccountDto().getUserId());
        then(articleCommentRepository).should().findByArticle_Id(articleId);
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
        //댓글을 저장하는 경우 해당 유저가 있는지 확인해야 함 그 과정에 대한 test
        //실제로 어떤 값이 return 되었는지는 사실 여기서는 상관 X
        //해당 Service 객체가 아래의 과정을 거치기 때문에 넣어줘야 함?
        given(userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId())).willReturn(createUserAccount());
        given(articleRepository.getReferenceById(dto.getArticleId())).willReturn(createArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        //When
        //댓글도 dto 로
        sut.saveArticleComment(dto);

        //Then


        //interaction 검사
        then(userAccountRepository).should().getReferenceById(dto.getUserAccountDto().getUserId());
        then(articleRepository).should().getReferenceById(dto.getArticleId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안 한다.")
    @Test
    void givenNonexistentArticle_whenSavingArticleComment_thenLogsSituationAndDoesNothing() {
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.getArticleId())).willThrow(EntityNotFoundException.class);
        //When
        sut.saveArticleComment(dto);
        //Then
        then(articleRepository).should().getReferenceById(dto.getArticleId());
        //게시글이 없으므로 해당 객체는 아무일도 하지 않았음을 test 로 확인
        then(userAccountRepository).shouldHaveNoInteractions();
        then(articleCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
    @Test
    void givenArticleCommentInfo_whenUpdatingArticleComment_thenUpdatesArticleComment() {
        //Given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent);
        Optional.of(articleComment);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
        given(articleCommentRepository.findById(dto.getId())).willReturn(Optional.of(articleComment));
        //When
        sut.updateArticleComment(dto);

        //Then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        then(articleCommentRepository).should().findById(dto.getId());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무 것도 안 한다.")
    @Test
    void givenNonexistentArticleComment_whenUpdatingArticleComment_thenLogsWarningAndDoesNothing() {
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleCommentRepository.findById(dto.getId())).willThrow(EntityNotFoundException.class);
        //When
        sut.updateArticleComment(dto);
        //Then
        then(articleCommentRepository).should().findById(dto.getId());
    }

    @Disabled("구현 고민중")
    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    @Test
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment() {
        //Given
        Long articleCommentId = 1L;
        BDDMockito.willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);
        //When
        sut.deleteArticleComment(articleCommentId);

        //Then
        then(articleCommentRepository).should().deleteById(articleCommentId);
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