package com.projectboard.domain;

import java.time.LocalDateTime;

//댓글
public class ArticleComment {
    private Long id;
    private Article article;
    private String content;

    //메타 데이터
    private LocalDateTime createdAt;
    private String createdBy;

    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
