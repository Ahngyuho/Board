package com.projectboard.dto.request;

import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.UserAccountDto;

public class ArticleFormRequest {
    public String title;
    public String content;
    public String hashtag;

    public ArticleFormRequest(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static ArticleFormRequest of(String title, String content, String hashtag) {
        return new ArticleFormRequest(title, content, hashtag);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return ArticleDto.builder()
                .userAccountDto(userAccountDto)
                .title(title)
                .content(content)
                .hashtag(hashtag)
                .build();
    }
}
