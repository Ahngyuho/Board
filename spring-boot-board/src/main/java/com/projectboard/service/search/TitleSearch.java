package com.projectboard.service.search;

import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class TitleSearch implements SearchStrategy {
    @Override
    public Page<ArticleDto> execute(ArticleRepository articleRepository, String searchKeyword, Pageable pageable) {
        return articleRepository.findByTitleContaining(searchKeyword,pageable).map(ArticleDto::from);
    }
}
