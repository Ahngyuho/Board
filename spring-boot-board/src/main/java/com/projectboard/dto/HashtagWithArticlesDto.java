package com.projectboard.dto;
import com.projectboard.domain.Hashtag;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
public class HashtagWithArticlesDto {
    Long id;
    Set<ArticleDto> articles;
    String hashtagName;
    LocalDateTime createdAt;
    String createdBy;
    LocalDateTime modifiedAt;
    String modifiedBy;

    @Builder
    public HashtagWithArticlesDto(Long id,
    Set<ArticleDto> articles,
    String hashtagName,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy) {
        this.id = id;
        this.articles = articles;
        this.hashtagName = hashtagName;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }


    public static HashtagWithArticlesDto of(Set<ArticleDto> articles, String hashtagName) {
        return new HashtagWithArticlesDto(null, articles, hashtagName, null, null, null, null);
    }

    public static HashtagWithArticlesDto of(Long id, Set<ArticleDto> articles, String hashtagName, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new HashtagWithArticlesDto(id, articles, hashtagName, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static HashtagWithArticlesDto from(Hashtag entity) {
        return new HashtagWithArticlesDto(
                entity.getId(),
                entity.getArticles().stream()
                        .map(ArticleDto::from)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                entity.getHashtagName(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Hashtag toEntity() {
        return Hashtag.of(hashtagName);
    }
}
