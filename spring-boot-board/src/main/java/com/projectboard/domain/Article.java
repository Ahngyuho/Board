package com.projectboard.domain;

import com.fasterxml.jackson.annotation.JsonTypeId;

import java.time.LocalDateTime;

//테이블 명은 복수여야 하는가?
//plural vs singular
//이번 프로젝트는 단수로 할 것임
//단수의 장점을 한번 알아보자
//게시글
public class Article {
    private Long id;
    private String title;
    private String content;
    private String hashtag;

    //메타 데이터
    private LocalDateTime createdAt;
    private String createdBy;

    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
