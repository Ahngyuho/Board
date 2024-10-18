package com.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "hashtagName", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Hashtag extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    //cascade 옵션 X
    //User 측에 Hashtag 를 관리하는 기능을 주지 않음
    @ManyToMany(mappedBy = "hashtags")
    //순서 개념이 있는 set
    private Set<Article> articles = new LinkedHashSet<>();

    @Setter
    @Column(nullable = false) private String hashtagName; // 해시태그 이름


    protected Hashtag() {}

    private Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    public static Hashtag of(String hashtagName) {
        return new Hashtag(hashtagName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hashtag that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
