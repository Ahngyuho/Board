package com.projectboard.controller;

import com.projectboard.config.SecurityConfig;
import com.projectboard.dto.ArticleWithCommentsDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@DisplayName("view 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    //test 에서는 생성자 하나있어도 @Autowired 생략 불가능하다
    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    //이 테스트가 api의 입출력만 보게끔 하기 위해 이 articleService와 연결을 끊기 위함
    @MockBean private ArticleService articleService;

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        //any()는 ArgumentMatchers 인데 이건 메서드 딱 하나의 파라미터에만 사용할 수 없음 그래서 모든 파라미터에 ArgumentMatchers 를 붙야즌갓
        given(articleService.searchArticles(ArgumentMatchers.eq(null),ArgumentMatchers.eq(null),ArgumentMatchers.any(Pageable.class))).willReturn(Page.empty());

        //When & Then
        mvc.perform(get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                //view 이름도 테스트 할 수 있음 디렉토리도 적어줘야함 article(디렉토리)/index(html)
                .andExpect(MockMvcResultMatchers.view().name("articles/index"))
                //modelattribute 가 있는지 없는지 검사 이 articles라는 key값이 있는지
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"));

        //이 should 는 1회 호출된다는 의미가 내포되어 있음 문서 읽어보면 됨
        then(articleService).should().searchArticles(ArgumentMatchers.eq(null),ArgumentMatchers.eq(null),ArgumentMatchers.any(Pageable.class));
    }

    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        //Given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

        //When & Then
        mvc.perform(get("/articles/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/detail"))
                //modelattribute 가 있는지 없는지 검사 이 article라는 key값이 있는지
                .andExpect(MockMvcResultMatchers.model().attributeExists("article"))
                //게시글 하나에는 여러 댓글들이 있어야 하므로...
                .andExpect(MockMvcResultMatchers.model().attributeExists("articleComments"));

        //articleService가 getArticle 을 한번 호출 해야될거다
        then(articleService).should().getArticle(articleId);
    }



    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("articles/search"))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "agh",
                LocalDateTime.now(),
                "agh"
        );
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