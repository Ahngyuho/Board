package com.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

//댓글
@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @ManyToOne(optional = false) // optional = false 면 이건 필수값이라는 뜻임
                                //cascade 는 none 댓글을 삭제해도 게시글에 영향가지 않도록 하기 위함
    private Article article;

//    @Setter @ManyToOne(optional = false) private UserAccount userAccount;
@Setter @ManyToOne(optional = false) @JoinColumn(name = "userId") private UserAccount userAccount; // 유저 정보 (ID)
    @Setter @Column(nullable = false,length = 500) private String content;


    protected ArticleComment() {

    }

    private ArticleComment(Article article, UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }

    //factory method
    public static ArticleComment of(Article article,UserAccount userAccount ,String content) {
        return new ArticleComment(article,userAccount,content);
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
