package com.mimawiki.api.service;

import com.mimawiki.api.dto.req.ArticleReq;
import com.mimawiki.api.dto.res.ArticleRes;
import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.ArticleLike;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.repository.ArticleLikeRepository;
import com.mimawiki.api.repository.ArticleRepository;
import com.mimawiki.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ArticleLikeRepository articleLikeRepository;

    @Transactional
    public ArticleRes createArticle(Long memberId, ArticleReq dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Article article = Article.builder()
                .keyword(dto.getKeyword())
                .markdown(dto.getMarkdown())
                .content(dto.getContent())
                .author(member)
                .build();

        Article saved = articleRepository.save(article);
        return ArticleRes.fromEntity(saved);
    }

    @Transactional
    public ArticleRes editArticle(Long id, ArticleReq dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (dto.getKeyword() != null) article.setKeyword(dto.getKeyword());
        if (dto.getMarkdown() != null) article.setMarkdown(dto.getMarkdown());
        if (dto.getContent() != null) article.setContent(dto.getContent());

        return ArticleRes.fromEntity(article);
    }

    @Transactional
    public boolean toggleLike(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (articleLikeRepository.existsByArticleAndMember(article, member)) {
            ArticleLike like = articleLikeRepository.findByArticleAndMember(article, member).get();
            articleLikeRepository.delete(like);
            return false;
        } else {
            ArticleLike like = ArticleLike.builder()
                    .article(article)
                    .member(member)
                    .build();
            articleLikeRepository.save(like);
            return true;
        }
    }

    public ArticleRes getArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        article.increaseViewCount();
        return ArticleRes.fromEntity(article);
    }
}
