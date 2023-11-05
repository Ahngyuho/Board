package com.projectboard.dto.request;

import com.projectboard.domain.ArticleComment;
import com.projectboard.dto.ArticleCommentDto;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.UserAccountDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ArticleCommentRequest {
    public Long articleId;
    public String content;

    @Builder
    public ArticleCommentRequest(Long articleId, String content) {
        this.articleId = articleId;
        this.content = content;
    }

    public static ArticleCommentRequest of(Long articleId, String content){
        return ArticleCommentRequest.builder()
                .articleId(articleId)
                .content(content)
                .build();
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto){
        return ArticleCommentDto.builder()
                .articleId(articleId)
                .userAccountDto(userAccountDto)
                .content(content)
                .build();
    }
}
