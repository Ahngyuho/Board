package com.projectboard.dto;

import com.projectboard.domain.Article;
import com.projectboard.domain.UserAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

//엔티티의 모든 정보를 들고 있게끔 함
//나중에 응답에 필요한 것들만 선택해서 보내게끔 할 것임
@Getter @Setter
public class ArticleDto {
    Long id;
    UserAccountDto userAccountDto;
    String title;
    String content;
    String hashtag;
    LocalDateTime createdAt;
    String createdBy;
    LocalDateTime modifiedAt;
    String modifiedBy;

    @Builder
    private ArticleDto(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        this.id = id;
        this.userAccountDto = userAccountDto;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    //엔티티를 입력하면 dto 로 반환
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    //dto 로 엔티티를 생성
    //이렇게 아래 두 메서드를 생성해 놓으면 도메인은 dto 를 의존하지 않음
    //그리고 이건 osiv 라는 개념과도 연관이 있음
    public Article toEntity(UserAccount userAccount){
        return Article.of(
                userAccount,
                //userAccountDto
                title,
                content,
                hashtag
        );
    }
}
