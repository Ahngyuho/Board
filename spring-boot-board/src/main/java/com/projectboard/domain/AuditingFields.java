package com.projectboard.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@ToString
@EntityListeners(AuditingEntityListener.class) //얘도 가져옴
@MappedSuperclass
public class AuditingFields {
    //여기에 중복되는 메타 데이터들을 가져옴
    //메타 데이터
    //자동으로 jpa 가 세팅
    //jpa auditing

    //웹 화면에 보여질 때 웹 화면에서 파라미터를 받아서 세팅될 때
    //파싱에 대한 룰을 넣어줘야 됨 파싱이 잘 되도록
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false,updatable = false) //이 필드는 업데이트 불가함을 추가해줌
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false,length = 100,updatable = false)//이 필드는 업데이트 불가함을 추가해줌
    private String createdBy;    //여기에 들어가게 될 값은 따로 설정

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(nullable = false,length = 100)
    private String modifiedBy;
}
