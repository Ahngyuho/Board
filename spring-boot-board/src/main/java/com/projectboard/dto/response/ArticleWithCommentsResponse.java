package com.projectboard.dto.response;

import com.projectboard.dto.ArticleWithCommentsDto;
import com.projectboard.dto.HashtagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class ArticleWithCommentsResponse{
    Long id;
    String title;
    String content;
    Set<String> hashtags;
    LocalDateTime createdAt;
    String email;
    String nickname;
    String userId;
    Set<ArticleCommentResponse> articleCommentsResponse;

    @Builder
    public ArticleWithCommentsResponse(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentsResponse) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hashtags = hashtags;
        this.createdAt = createdAt;
        this.email = email;
        this.nickname = nickname;
        this.userId = userId;
        this.articleCommentsResponse = articleCommentsResponse;
    }

    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId,Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId,articleCommentResponses);
    }

    //from 엔티티 -> dto
    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.getUserAccountDto().getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getUserAccountDto().getUserId();
        }

        return new ArticleWithCommentsResponse(
                dto.getId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getHashtags().stream()
                        .map(HashtagDto::getHashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.getCreatedAt(),
                dto.getUserAccountDto().getEmail(),
                nickname,
                dto.getUserAccountDto().getUserId(),
                dto.getArticleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
