package com.projectboard.controller;

import com.projectboard.config.SecurityConfig;
import com.projectboard.config.TestSecurityConfig;
import com.projectboard.domain.constant.FormStatus;
import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.ArticleWithCommentsDto;
import com.projectboard.dto.HashtagDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.dto.request.ArticleFormRequest;
import com.projectboard.dto.response.ArticleResponse;
import com.projectboard.dto.security.BoardPrincipal;
import com.projectboard.repository.JpaRepositoryTest;
import com.projectboard.service.ArticleService;
import com.projectboard.service.PaginationService;
import com.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("view 컨트롤러 - 게시글")
//이제 SecurityConfig 는 사용이 힘들다
@Import({TestSecurityConfig.class,FormDataEncoder.class})
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    //test 에서는 생성자 하나있어도 @Autowired 생략 불가능하다
    public ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    //이 테스트가 api의 입출력만 보게끔 하기 위해 이 articleService와 연결을 끊기 위함
    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        //any()는 ArgumentMatchers 인데 이건 메서드 딱 하나의 파라미터에만 사용할 수 없음 그래서 모든 파라미터에 ArgumentMatchers 를 붙야즌갓
        given(articleService.searchArticles(ArgumentMatchers.eq(null),ArgumentMatchers.eq(null),ArgumentMatchers.any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));
        //When & Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                //view 이름도 테스트 할 수 있음 디렉토리도 적어줘야함 article(디렉토리)/index(html)
                .andExpect(view().name("articles/index"))
                //modelattribute 가 있는지 없는지 검사 이 articles라는 key값이 있는지
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationNumbers"));

        //이 should 는 1회 호출된다는 의미가 내포되어 있음 문서 읽어보면 됨
        then(articleService).should().searchArticles(ArgumentMatchers.eq(null),ArgumentMatchers.eq(null),ArgumentMatchers.any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 검색어와 함께 정상 호출")
    @Test
    void giveKeyWord_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        //any()는 ArgumentMatchers 인데 이건 메서드 딱 하나의 파라미터에만 사용할 수 없음 그래서 모든 파라미터에 ArgumentMatchers 를 붙야즌갓
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        //eq(searchType),eq(searchValue)는 pageable 은 아무거나 받아도 된다는 의미이다.그렇게 한 이유는 지금 여기 test 의 목적은 검색에만 초점을 맞추었기 때문  그래서 matchers 를 맞춰준것
        given(articleService.searchArticles(ArgumentMatchers.eq(searchType),ArgumentMatchers.eq(searchValue),ArgumentMatchers.any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));
        //When & Then
        mvc.perform(get("/articles")
                        .queryParam("searchType", searchType.name())
                        .queryParam("searchValue", searchValue)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                //view 이름도 테스트 할 수 있음 디렉토리도 적어줘야함 article(디렉토리)/index(html)
                .andExpect(view().name("articles/index"))
                //modelattribute 가 있는지 없는지 검사 이 articles라는 key값이 있는지
                .andExpect(model().attributeExists("articles"))
//                .andExpect(model().attributeExists("paginationNumbers"));
                //서버에서 searchTypes 들을 보여서 렌더링 해줄것임
        .andExpect(model().attributeExists("searchTypes"));

        //이 should 는 1회 호출된다는 의미가 내포되어 있음 문서 읽어보면 됨
        then(articleService).should().searchArticles(ArgumentMatchers.eq(searchType),ArgumentMatchers.eq(searchValue),ArgumentMatchers.any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnArticle() throws Exception {
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(),Page.empty().getTotalPages())).willReturn(barNumbers);

        mvc.perform(
                get("/articles")
                        .queryParam("page",String.valueOf(pageNumber))
                        .queryParam("size",String.valueOf(pageSize))
                        .queryParam("sort",sortName + "," + direction)

        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationNumbers",barNumbers));
        then(articleService).should().searchArticles(null,null,pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(),Page.empty().getTotalPages());
    }


    @WithMockUser
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        //Given
        Long articleId = 1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());

        //When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                //modelattribute 가 있는지 없는지 검사 이 article라는 key값이 있는지
                .andExpect(model().attributeExists("article"))
                //게시글 하나에는 여러 댓글들이 있어야 하므로...
                .andExpect(model().attributeExists("articleComments"));

        //articleService가 getArticle 을 한번 호출 해야될거다
        then(articleService).should().getArticleWithComments(articleId);
    }

    @DisplayName("[view][GET] 게시글 페이지 - 인증 정보 없는 경우 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequestingArticlePage_thenRedirectsLoginPage() throws Exception{
        long articleId = 1L;

        //when & then
        mvc.perform(
                get("/articles/" + articleId)
        ).andExpect(status().is3xxRedirection())
                //redirect 관련 assertions? method
                //redirectdUrlPattern 은 full 로 작성해야 하는 url 을
                //** 을 통해 간략히 작성 가능하게 해준다.
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
        then(articleService).shouldHaveNoInteractions();
    }

    //그냥 이렇게 해주면 알아서 인증 정보를 가져와줌
    @WithMockUser
    @DisplayName("[view][GET] 게시글 페이지 - 정상호출, 인증된 사용자")
    @Test
    void givenNothing_whenRequestingArticlePage_thenReturnArtilceView() throws Exception{
        long articleId = 1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
//
        //when & then
        mvc.perform(
                        get("/articles/" + articleId)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticleWithComments(articleId);
    }



    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/search"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    void giveNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        //Given
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        //When & Then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attributeExists("hashtags"))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
        then(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class)));
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    void giveHashtag_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        //Given
        String hashtag = "#java";
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        //When & Then
        mvc.perform(get("/articles/search-hashtag").queryParam("searchValue", hashtag))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attributeExists("hashtags"))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
        then(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class)));
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 작성 페이지 호출 - 정상 호출")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception{
        //given

        //when & Then
        mvc.perform(
                        get("/articles/form")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    //WithUserDetails 는 UserDetails 를 사용할 수 있게 해줌
    //setupBefore 는 해당 애너테이션 적용 시점이다.
    //이걸 넣어주면 perform(post().with() <- user 정보를 넣어주지 않아도 됨.
    @WithUserDetails(value = "aghTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSaveNewArticle() throws Exception{
        //given
        ArticleFormRequest articleRequest = ArticleFormRequest.of("new title", "new content");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        //when & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
//                        .with(user())
                ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 수정 페이지 호출 - 정상 호출")
    @Test
    void givenArticleId_whenRequesting_thenReturnsNewArticlePage() throws Exception{
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();

        given(articleService.getArticle(articleId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(articleService).should().getArticle(articleId);
    }

    @WithUserDetails(value = "aghTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesNewArticle() throws Exception {
        // Given
        long articleId = 1L;
        ArticleFormRequest articleRequest = ArticleFormRequest.of("new title", "new content");
        willDoNothing().given(articleService).updateArticle(eq(articleId), any(ArticleDto.class));

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }

    @WithUserDetails(value = "aghTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeleteArticle() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "aghTest";
        willDoNothing().given(articleService).deleteArticle(articleId,userId);

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().deleteArticle(articleId,userId);
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.builder()
                .userAccountDto(createUserAccountDto())
                .title("title")
                .content("content")
                .hashtags(Set.of(HashtagDto.of("java")))
                .build();
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                Set.of(HashtagDto.of("java")),
                LocalDateTime.now(),
                "agh",
                LocalDateTime.now(),
                "agh"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "agh",
                LocalDateTime.now(),
                "agh"
        );
    }
}