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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ArticleLikeRepository articleLikeRepository;

    /**
     * 글 작성
     */
    @Transactional
    public ArticleRes createArticle(Long memberId, ArticleReq dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // keyword 중복 체크
        if (articleRepository.findByKeyword(dto.getKeyword()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 키워드입니다.");
        }

        Article article = Article.builder()
                .keyword(dto.getKeyword())
                .markdown(dto.getMarkdown())
                .content(dto.getContent())
                .author(member)
                .build();

        Article saved = articleRepository.save(article);
        return ArticleRes.fromEntity(saved);
    }

    /**
     * 글 조회 (keyword로)
     */
    @Transactional
    public ArticleRes getArticleByKeyword(String keyword) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        article.increaseViewCount();
        return ArticleRes.fromEntity(article);
    }

    /**
     * 글 검색 (keyword LIKE 검색, 페이징)
     * keyword가 null이면 전체 조회
     */
    public Page<ArticleRes> searchArticles(String keyword, Pageable pageable) {
        Page<Article> articles;

        if (keyword == null || keyword.isBlank()) {
            // keyword가 없으면 전체 조회
            articles = articleRepository.findAll(pageable);
        } else {
            // keyword가 있으면 LIKE 검색
            articles = articleRepository.findByKeywordContaining(keyword, pageable);
        }

        return articles.map(ArticleRes::fromEntity);
    }

    /**
     * 글 직접 수정 (작성자 또는 관리자만 가능)
     */
    @Transactional
    public ArticleRes editArticle(String keyword, Long memberId, ArticleReq dto) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 권한 체크: 작성자 또는 관리자만 가능
        boolean isAuthor = article.getAuthor().getId().equals(memberId);
        boolean isAdmin = member.getRole() == Member.Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        if (dto.getKeyword() != null) article.setKeyword(dto.getKeyword());
        if (dto.getMarkdown() != null) article.setMarkdown(dto.getMarkdown());
        if (dto.getContent() != null) article.setContent(dto.getContent());
        article.setModifiedBy(member.getEmail());

        return ArticleRes.fromEntity(article);
    }

    /**
     * 글 삭제 (작성자 또는 관리자만 가능)
     */
    @Transactional
    public void deleteArticle(String keyword, Long memberId) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 권한 체크: 작성자 또는 관리자만 가능
        boolean isAuthor = article.getAuthor().getId().equals(memberId);
        boolean isAdmin = member.getRole() == Member.Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        articleRepository.delete(article);
    }

    /**
     * 좋아요 토글 (keyword 기반)
     */
    @Transactional
    public boolean toggleLike(String keyword, Long memberId) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<ArticleLike> existingLike = articleLikeRepository.findByArticleAndMember(article, member);

        if (existingLike.isPresent()) {
            articleLikeRepository.delete(existingLike.get());
            article.decreaseLikeCount();
            return false; // 좋아요 취소
        } else {
            ArticleLike like = ArticleLike.builder()
                    .article(article)
                    .member(member)
                    .build();
            articleLikeRepository.save(like);
            article.increaseLikeCount();
            return true; // 좋아요 추가
        }
    }

    /**
     * 실시간 인기 차트 (좋아요 많은 순 10개)
     */
    public List<ArticleRes> getPopularArticles() {
        List<Article> articles = articleRepository.findTop10ByOrderByLikeCountDesc();
        return articles.stream()
                .map(ArticleRes::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ArticleRes getArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        article.increaseViewCount();
        return ArticleRes.fromEntity(article);
    }
}