package com.projectboard.controller;

import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.response.ArticleResponse;
import com.projectboard.dto.response.ArticleWithCommentsResponse;
import com.projectboard.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public String articles(
            //required = false 는 필수는 아니라는 것임
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            //페이징 디폴트 설정
            @PageableDefault(size = 10, sort = "createdAt" , direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ){
        map.addAttribute("articles", articleService.
                //SearchArticles 의 반환은 AricleDto 임 이건 모든 필드를 다 들고 있어서
                searchArticles(searchType,searchValue,pageable).
                //다시 응답에 필요한 dto로 변환하는 과정이 필요함
                map(ArticleResponse::from));
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map){

        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
        map.addAttribute("article", article);
        map.addAttribute("articleComments",article.articleCommentsResponse());
        return "articles/detail";
    }

}
