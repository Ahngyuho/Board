package com.projectboard.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Spring Data REST 통합테스트는 불필요하므로 제외시킴")
@DisplayName("Data REST - API 테스트")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class DataRestTest {
    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticles_whenReturnsArticlesJsonResponse() throws Exception {
        //Given

        //When & Then
        //그냥 이 endpoint 가 존재하고 잘 동작하는지 확인하는 test
        //data rest 기능을 그대로 사용      //이건 리스트 조회임
        mvc.perform(get("/api/articles")) //static import 해준것이다 MockMvcRequestBuilders.get
                .andExpect(status().isOk()) //static import 해준것이다 MockMvcResultMatchers.content
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));//static import 해준것이다 MockMvcResultMatchers.status
        //ctrl + shift + space 해주면 추천이 뜬다
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticle_whenReturnsArticlesJsonResponse() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 게시글 -> 댓글 조회")
    @Test
    void givenNothing_whenRequestingArticleCommentsFromArticle_whenReturnsArticleCommentsJsonResponse() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/articles/1/articleComments")) //해당 게시글에서 댓글을 보는 것임
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleComments_whenReturnsArticleCommentsJsonResponse() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/articleComments")) //해당 게시글에서 댓글을 보는 것임
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticleComment_whenReturnsArticleCommentJsonResponse() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/articleComments/1")) //해당 게시글에서 댓글을 보는 것임
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }
}
