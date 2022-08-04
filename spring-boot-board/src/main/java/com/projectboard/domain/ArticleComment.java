package com.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

//댓글
@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ArticleComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @ManyToOne(optional = false) // optional = false 면 이건 필수값이라는 뜻임
                                //cascade 는 none 댓글을 삭제해도 게시글에 영향가지 않도록 하기 위함
    private Article article;
    @Setter @Column(nullable = false,length = 500) private String content;

    //메타 데이터
    //자동으로 jpa 가 세팅
    //jpa auditing
    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt;
    @CreatedBy @Column(nullable = false,length = 100) private String createdBy;    //여기에 들어가게 될 값은 따로 설정

    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt;
    @LastModifiedBy @Column(nullable = false,length = 100) private String modifiedBy;

    protected ArticleComment() {

    }

    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    //factory method
    public static ArticleComment of(Article article, String content) {
        return new ArticleComment(article,content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
