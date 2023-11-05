package com.projectboard.controller;

import com.projectboard.domain.Article;
import com.projectboard.domain.constant.FormStatus;
import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.UserAccountDto;
import com.projectboard.dto.request.ArticleFormRequest;
import com.projectboard.dto.response.ArticleResponse;
import com.projectboard.dto.response.ArticleWithCommentsResponse;
import com.projectboard.dto.security.BoardPrincipal;
import com.projectboard.service.ArticleService;
import com.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
            //required = false 는 필수는 아니라는 것임
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            //페이징 디폴트 설정
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) throws IllegalAccessException {
        Page<ArticleResponse> articleResponses = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        articleResponses.getSort().getOrderFor("userAccount.userId");
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articleResponses.getTotalPages());
        //articleService.//SearchArticles 의 반환은 AricleDto 임 이건 모든 필드를 다 들고 있어서searchArticles(searchType,searchValue,pageable).//다시 응답에 필요한 dto로 변환하는 과정이 필요함map(ArticleResponse::from)
        map.addAttribute("articles", articleResponses);
        map.addAttribute("paginationNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        //지금 여기서 쿼리가 두방 나감
        //article - articleComment 를 동시에 조회해서 그럼
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.getArticleCommentsResponse());

        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(@RequestParam(required = false) String searchValue,
                                       @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                       ModelMap map) {
        Page<ArticleResponse> articleResponses = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articleResponses.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        map.addAttribute("articles", articleResponses);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
//        map.addAttribute("searchTypes", SearchType.values());

        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(ArticleFormRequest articleFormRequest,
                                 @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleService.saveArticle(articleFormRequest.toDto(boardPrincipal.toDto()));
        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(
            @PathVariable Long articleId, ArticleFormRequest articleFormRequest,
        @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        articleService.updateArticle(articleId, articleFormRequest.toDto(boardPrincipal.toDto()));
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        articleService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }

}
