package com.projectboard.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@DisplayName("view 컨트롤러 - 게시글")
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    //test 에서는 생성자 하나있어도 @Autowired 생략 불가능하다
    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                //view 이름도 테스트 할 수 있음 디렉토리도 적어줘야함 article(디렉토리)/index(html)
                .andExpect(MockMvcResultMatchers.view().name("articles/index"))
                //modelattribute 가 있는지 없는지 검사 이 articles라는 key값이 있는지
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"));
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/detail"))
                //modelattribute 가 있는지 없는지 검사 이 article라는 key값이 있는지
                .andExpect(MockMvcResultMatchers.model().attributeExists("article"))
                //게시글 하나에는 여러 댓글들이 있어야 하므로...
                .andExpect(MockMvcResultMatchers.model().attributeExists("articleComments"));
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

}