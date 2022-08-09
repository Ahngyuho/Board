package com.projectboard.service;

import com.projectboard.dto.ArticleCommentDto;
import com.projectboard.repository.ArticleCommentRepository;
import com.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return List.of();
    }

    public void saveArticleComment(ArticleCommentDto dto){

    }

    public void updateArticleComment(ArticleCommentDto dto){

    }

    public void deleteArticleComment(Long articleCommentId){

    }

}
