package com.projectboard.dto.request;

import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.HashtagDto;
import com.projectboard.dto.UserAccountDto;

import java.util.Set;

public class ArticleFormRequest {
    public String title;
    public String content;

    public ArticleFormRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static ArticleFormRequest of(String title, String content) {
        return new ArticleFormRequest(title, content);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return toDto(userAccountDto, null);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {
        return ArticleDto.builder()
                .userAccountDto(userAccountDto)
                .title(title)
                .content(content)
                .hashtags(hashtagDtos)
                .build();
    }
}
