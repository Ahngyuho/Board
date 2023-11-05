package com.projectboard.service.search;

import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@FunctionalInterface
public interface SearchStrategy {
    Page<ArticleDto> execute(ArticleRepository articleRepository, String searchKeyword, Pageable pageable);
}
