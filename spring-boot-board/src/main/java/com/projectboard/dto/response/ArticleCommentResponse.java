package com.projectboard.dto.response;

import com.projectboard.dto.ArticleCommentDto;

import java.io.Serializable;
import java.time.LocalDateTime;

//dto
//다른 dto 들고 다르게 정보를 엔티티의 일부만 들고 있음
//댓글 요청으로 나가는 dto 임
//컨트롤러에 사용
public record ArticleCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname
) implements Serializable {
    public static ArticleCommentResponse of(
            Long id,
            String content,
            LocalDateTime createdAt,
            String email,
            String nickname
    ){
        return new ArticleCommentResponse(id, content, createdAt, email, nickname);
    }

    //nickname 같은 경우는 안 들어갈 수도 있는 필드라 이렇게 UserAccount 에 있는 정보를 dto 를 텅해 전달
    public static ArticleCommentResponse from(ArticleCommentDto dto){
        String nickname = dto.userAccountDto().nickname();

        if(nickname == null || nickname.isBlank()){
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleCommentResponse(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }
}
