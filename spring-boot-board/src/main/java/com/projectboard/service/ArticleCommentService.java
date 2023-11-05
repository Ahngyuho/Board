package com.projectboard.service;

import com.projectboard.domain.Article;
import com.projectboard.domain.ArticleComment;
import com.projectboard.domain.UserAccount;
import com.projectboard.dto.ArticleCommentDto;
import com.projectboard.dto.ArticleDto;
import com.projectboard.repository.ArticleCommentRepository;
import com.projectboard.repository.ArticleRepository;
import com.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId).stream().map(ArticleCommentDto::from).toList();
    }

    public void saveArticleComment(ArticleCommentDto dto){
        try {
            Article article = articleRepository.getReferenceById(dto.getArticleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId());
            articleCommentRepository.save(dto.toEntity(article, userAccount));
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글 작성에 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }

    }

    public void updateArticleComment(ArticleCommentDto dto){
        try {
            ArticleComment articleComment = articleCommentRepository.findById(dto.getId()).get();
            if (dto.getContent() != null) { articleComment.setContent(dto.getContent()); }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }

    public void deleteArticleComment(Long articleCommentId) {
        articleCommentRepository.deleteById(articleCommentId);
    }

}
