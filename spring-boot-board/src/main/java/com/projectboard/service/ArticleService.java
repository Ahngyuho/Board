package com.projectboard.service;

import com.projectboard.domain.Article;
import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.ArticleWithCommentsDto;
import com.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)//Paging 위한 Pageable 추가
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        //searchKeyword 가 쿼리에 들어감
        if(searchKeyword == null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        //이제 searchType 을 기반으로 서로 다른 쿼리를 만들어야 함
        //제목 id ... 쿼리
        //리팩토링 여지를 남겨둠
        return switch (searchType){
            //java 11 이후 case 타입을 추천해줌 : 아니라 -> 로
            //이제 switch 가 return 도 가능함
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword,pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword,pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword,pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword,pageable).map(ArticleDto::from);
            //#을 해준 이유는 검색할 때 자동으로 #이 들어가도록 하기 위함
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword,pageable).map(ArticleDto::from);
        };
    }

    //단건 조회
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId){
        //게시글을 조회하고 양방향 연관관계로 걸려있는 ArticleComment 도 같이 불러와야됨
        //그 dto 가 ArticleWithCommentDto 임
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                //이게 Optional 로 반환돼서 이런식으로 해줘야 됨 예외는 있는거 사용
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }


    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    //update 그냥 ArticleDto 사용
    public void updateArticle(ArticleDto dto) {
        //이 getReferenceById 는 EntityNotFoundException 예외를 터트린다.
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            //dto.title() 는 get 과 같은 것 dto 가 public record 로 되어있어서 새로 적용된 내용임
            if (dto.title() != null) {
                article.setTitle(dto.title());
            }
            if (dto.content() != null) {
                article.setContent(dto.content());
            }
            article.setHashtag(dto.hashtag());
        }catch (EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}",dto);
        }
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }
}
