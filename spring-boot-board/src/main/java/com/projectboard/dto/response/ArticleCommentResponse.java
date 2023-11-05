package com.projectboard.dto.response;

import com.projectboard.dto.ArticleCommentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

//dto
//다른 dto 들고 다르게 정보를 엔티티의 일부만 들고 있음
//댓글 요청으로 나가는 dto 임
//컨트롤러에 사용
@Getter @Setter
public class ArticleCommentResponse  {
    Long id;
    String content;
    LocalDateTime createdAt;
    String email;
    String nickname;
    String userId;

    @Builder
    public ArticleCommentResponse(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.email = email;
        this.nickname = nickname;
        this.userId = userId;
    }

    public static ArticleCommentResponse of(
            Long id,
            String content,
            LocalDateTime createdAt,
            String email,
            String nickname,
            String userId
    ){
        return new ArticleCommentResponse(id, content, createdAt, email, nickname,userId);
    }

    //nickname 같은 경우는 안 들어갈 수도 있는 필드라 이렇게 UserAccount 에 있는 정보를 dto 를 텅해 전달
    public static ArticleCommentResponse from(ArticleCommentDto dto){
        String nickname = dto.getUserAccountDto().getNickname();

        if(nickname == null || nickname.isBlank()){
            nickname = dto.getUserAccountDto().getUserId();
        }

        return new ArticleCommentResponse(
                dto.getId(),
                dto.getContent(),
                dto.getCreatedAt(),
                dto.getUserAccountDto().getEmail(),
                nickname,
                dto.getUserAccountDto().getUserId()
        );
    }
}
