package com.projectboard.dto.response;

import com.projectboard.dto.ArticleCommentDto;
import com.projectboard.dto.ArticleDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
public class ArticleResponse{
    Long id;
    String title;
    String content;
    String hashtag;
    LocalDateTime createdAt;
    String email;
    String nickname;

    @Builder
    private ArticleResponse(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
        this.createdAt = createdAt;
        this.email = email;
        this.nickname = nickname;
    }

    public static ArticleResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, email, nickname);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.getUserAccountDto().getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getUserAccountDto().getUserId();
        }

        return new ArticleResponse(
                dto.getId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getHashtag(),
                dto.getCreatedAt(),
                dto.getUserAccountDto().getEmail(),
                nickname
        );
    }
}
