package com.projectboard.service.search;

import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class SearchStrategyFactory {
    private final EnumMap<SearchType, SearchStrategy> strategies = new EnumMap<>(SearchType.class);

    public static SearchStrategy createSearchStrategy(SearchType searchType) {
        SearchStrategy searchStrategy;
        switch (searchType) {
            case TITLE:
                return (articleRepository, searchKeyword, pageable) ->
                        articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT:
                return (articleRepository, searchKeyword, pageable) ->
                        articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID:
                return (articleRepository, searchKeyword, pageable) ->
                        articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME:
                return (articleRepository, searchKeyword, pageable) ->
                        articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG:
                return (articleRepository, searchKeyword, pageable) ->
                        articleRepository.findByHashtag(searchKeyword, pageable).map(ArticleDto::from);
            default:
                throw new IllegalArgumentException("Invalid SearchType");
        }
    }

    public SearchStrategyFactory() {
        strategies.put(SearchType.TITLE, (articleRepository, searchKeyword, pageable) ->
                articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from));

        strategies.put(SearchType.CONTENT, (articleRepository, searchKeyword, pageable) ->
                articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from));

        strategies.put(SearchType.ID, (articleRepository, searchKeyword, pageable) ->
                articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from));

        strategies.put(SearchType.NICKNAME, (articleRepository, searchKeyword, pageable) ->
                articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from));

        strategies.put(SearchType.HASHTAG, (articleRepository, searchKeyword, pageable) ->
                articleRepository.findByHashtag(searchKeyword, pageable).map(ArticleDto::from));
    }

    public SearchStrategy createSearch(SearchType searchType) {
        if (searchType == null) {
            throw new IllegalArgumentException("Invalid Search Type");
        }
        return strategies.get(searchType);
    }
}
