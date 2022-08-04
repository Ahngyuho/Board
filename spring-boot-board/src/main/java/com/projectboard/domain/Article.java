package com.projectboard.domain;

import com.fasterxml.jackson.annotation.JsonTypeId;
import lombok.Generated;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

//테이블 명은 복수여야 하는가?
//plural vs singular
//이번 프로젝트는 단수로 할 것임
//단수의 장점을 한번 알아보자
//게시글
//전체 클래스에 setter 열어두지 않음 자동으로 값이 들어가는 필드들이 있기 때문임
@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    //mysql 의 auto increment 는 IDENTITY 방식임 이걸 해줘야함
    private Long id;

    @Setter @Column(nullable = false) private String title;
    @Setter @Column(nullable = false, length = 10000) private String content;

    @Setter @Column private String hashtag;

    //one to many 관계임
    //딱 한번만 세팅할 것이므로 이렇게 설정
    //set 을 사용 이 Article 에 연결된 comment 들은 중복을 허용하지 않고
    //여기에 모아서 컬렉션으로 만들어 보겠다는 것임
    @OneToMany(mappedBy = "article",cascade = CascadeType.ALL)    //"article"테이블과 연관된 것임을 알려줌 mappedBy 를 통해
    //cascade all 해준것은 게시글이 삭제되면 연관된 댓글들은 삭제
    //하지만 서비스 운영측면에서는 백업 목적으로 남겨둬야 해서 공부할 때만 이렇게 하자
    @OrderBy("id") //정렬 기준
    @ToString.Exclude//그리고 이렇게 해주는 이유는 무한 참조를 막기 위함도 있다
                        //그리고 이 경우 One 쪽에 .Exclude해준 이유는 댓글(N)로부터 글을 참조하는 경우는 정상적이나
                        //1쪽에서 댓글 리스트를 모두 뽑아보는 것은 굳이 그럴 필요가 없음
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();



    //추출은 크게 두가지로 가능
    //@Embedded or mappedsuperclass

    protected Article(){

    }

    //이 생성자를 private 로 만들자
    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    //의도를 전달가능 도메인 Article을 생성하고자 할때
    //어떤 값을 필요로 한다는 것을 이것으로 가이드 해주는 것
    //제목 본문 해시태그를 넣어달라고 가이드 해준것
    public static Article of(String title, String content, String hashtag) {
        return new Article(title,content,hashtag);
    }

    //만약 이걸 list 에 담아서 사용한다면 어떨까
    //게시글에 list 를 이용해서 게시판 화면을 구성한다던지
    //list 에 넣거나 중복요소 제거 정렬 등 비교를 해야할 수 있어야함
    //그래서 equals 나 hashcode 를 구현해야 함


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;

        //id != null 은 막 만들어진 영속화 되지않고 db 에도 없는 것은
        //모두 동등성 검사를 false 함
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
