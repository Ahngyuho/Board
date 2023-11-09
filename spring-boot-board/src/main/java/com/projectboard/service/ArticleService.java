package com.projectboard.service;

import com.projectboard.domain.Article;
import com.projectboard.domain.UserAccount;
import com.projectboard.domain.type.SearchType;
import com.projectboard.dto.ArticleDto;
import com.projectboard.dto.ArticleWithCommentsDto;
import com.projectboard.repository.ArticleRepository;
import com.projectboard.repository.UserAccountRepository;
import com.projectboard.service.search.Search;
import com.projectboard.service.search.SearchStrategy;
import com.projectboard.service.search.SearchStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.projectboard.service.search.SearchStrategyFactory.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final SearchStrategyFactory searchStrategyFactory;
    @Transactional(readOnly = true)//Paging 위한 Pageable 추가
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable)  {
        //searchKeyword 가 쿼리에 들어감
        if(searchKeyword == null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
//        SearchStrategy search = createSearchStrategy(searchType);
        Search search = new Search(searchStrategyFactory.createSearch(searchType));
        return search.execute(articleRepository, searchKeyword, pageable);
    }

    //단건 조회
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId){
        //게시글을 조회하고 양방향 연관관계로 걸려있는 ArticleComment 도 같이 불러와야됨
        //그 dto 가 ArticleWithCommentDto 임
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                //이게 Optional 로 반환돼서 이런식으로 해줘야 됨 예외는 있는거 사용
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }


    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId());
        articleRepository.save(dto.toEntity(userAccount));
    }

    //update 그냥 ArticleDto 사용
    public void updateArticle(Long articleId,ArticleDto dto) {
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId());

        //이 getReferenceById 는 EntityNotFoundException 예외를 터트린다.
        try {
            if(article.getUserAccount().equals(userAccount)){
                //            Article article = articleRepository.getReferenceById(articleId);
                //dto.title() 는 get 과 같은 것 dto 가 public record 로 되어있어서 새로 적용된 내용임
                if (dto.getTitle() != null) {
                    article.setTitle(dto.getTitle());
                }
                if (dto.getContent() != null) {
                    article.setContent(dto.getContent());
                }
                article.setContent(dto.getContent());
            }
        }catch (EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}",dto);
        }
    }

    public void deleteArticle(long articleId,String userId) {
//        articleRepository.deleteById(articleId);
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if (hashtag == null || hashtag.isBlank()) {
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtagNames(null, pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }
}
