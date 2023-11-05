package com.projectboard.service.search;

import com.projectboard.dto.ArticleDto;
import com.projectboard.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.projectboard.domain.type.SearchType.TITLE;

public class Search {
    private final SearchStrategy searchStrategy;

    public Search(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public Page<ArticleDto> execute(ArticleRepository articleRepository, String searchType, Pageable pageable) {
        return searchStrategy.execute(articleRepository, searchType, pageable);
    }
}
