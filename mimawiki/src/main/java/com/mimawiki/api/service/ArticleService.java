package com.mimawiki.api.service;

import com.mimawiki.api.dto.req.ArticleReq;
import com.mimawiki.api.dto.res.ArticleRes;
import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.ArticleLike;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.entity.Tag;
import com.mimawiki.api.repository.ArticleLikeRepository;
import com.mimawiki.api.repository.ArticleRepository;
import com.mimawiki.api.repository.MemberRepository;
import com.mimawiki.api.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ArticleLikeRepository articleLikeRepository;

    // ✅ [추가] 태그 리포지토리 주입
    private final TagRepository tagRepository;

    /**
     * 글 작성
     */
    @Transactional
    public ArticleRes createArticle(Long memberId, ArticleReq dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        // ✅ [추가] 태그 문자열 리스트 -> Tag 엔티티 Set으로 변환
        Set<Tag> tags = processTags(dto.getTags());

        Article article = Article.builder()
                .keyword(dto.getKeyword())
                .markdown(dto.getMarkdown())
                .content(dto.getContent())
                .author(member)
                .tags(tags) // ✅ [추가] 빌더에 태그 포함
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
     */
    public Page<ArticleRes> searchArticles(String keyword, Pageable pageable) {
        Page<Article> articles;

        if (keyword == null || keyword.isBlank()) {
            // 검색어 없으면 전체 조회
            articles = articleRepository.findAll(pageable);
        } else {
            // ✅ 통합 검색 쿼리 호출 (searchByComplex)
            articles = articleRepository.searchByComplex(keyword, pageable);
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

        // 권한 체크
        boolean isAuthor = article.getAuthor().getId().equals(memberId);
        boolean isAdmin = member.getRole() == Member.Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        if (dto.getKeyword() != null) article.setKeyword(dto.getKeyword());
        if (dto.getMarkdown() != null) article.setMarkdown(dto.getMarkdown());
        if (dto.getContent() != null) article.setContent(dto.getContent());

        // ✅ [추가] 태그 수정 로직
        // 요청에 태그가 포함되어 있다면(null이 아니면) 기존 태그를 갈아끼움
        if (dto.getTags() != null) {
            Set<Tag> newTags = processTags(dto.getTags());
            article.setTags(newTags);
        }

        article.setModifiedBy(member.getEmail());

        return ArticleRes.fromEntity(article);
    }

    /**
     * 글 삭제
     */
    @Transactional
    public void deleteArticle(String keyword, Long memberId) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean isAuthor = article.getAuthor().getId().equals(memberId);
        boolean isAdmin = member.getRole() == Member.Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }
        articleRepository.delete(article);
    }

    /**
     * 좋아요 토글
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

    private Set<Tag> processTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>(); // 태그가 없으면 빈 Set 반환
        }

        // 중복 입력 제거 (예: 사용자가 "Java", "Java" 입력 시 하나만 처리)
        Set<String> uniqueTagNames = new HashSet<>(tagNames);
        Set<Tag> tags = new HashSet<>();

        for (String tagName : uniqueTagNames) {
            // 1. 태그 이름으로 DB 조회
            // 2. 없으면(.orElseGet) -> 새 Tag 객체 생성 후 save -> 반환
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            tags.add(tag);
        }
        return tags;
    }

    public Page<ArticleRes> searchArticlesByTag(String tagName, Pageable pageable) {
        // 1. 리포지토리 호출 (Entity인 Page<Article> 반환됨)
        Page<Article> articles = articleRepository.findByTags_Name(tagName, pageable);

        // 2. 가공 (Entity -> DTO 변환)
        // Page 객체의 map 메서드를 사용하면 내부의 Article 리스트를 하나씩 ArticleRes로 바꿔줍니다.
        return articles.map(ArticleRes::fromEntity);
    }

    /**
     * 전체 게시글 조회 (페이징)
     * GET /api/mima.wiki/article?page=0
     */
    public Page<ArticleRes> getAllArticles(Pageable pageable) {
        // JPA 기본 제공 findAll 메서드 사용
        Page<Article> articles = articleRepository.findAll(pageable);

        // Entity -> DTO 변환
        return articles.map(ArticleRes::fromEntity);
    }
}