package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.ArticleReq;
import com.mimawiki.api.dto.res.ArticleRes;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.service.ArticleService;
import com.mimawiki.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mima.wiki")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final MemberService memberService;

    /**
     * 글 작성
     * POST /api/mima.wiki/write
     */
    @PostMapping("/write")
    public ResponseEntity<ArticleRes> createArticle(
            @RequestBody ArticleReq dto,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        ArticleRes response = articleService.createArticle(memberId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 글 조회 (키워드로)
     * GET /api/mima.wiki/article/{keyword}
     */
    @GetMapping("/article/{keyword}")
    public ResponseEntity<ArticleRes> getArticle(@PathVariable String keyword) {
        ArticleRes response = articleService.getArticleByKeyword(keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * 글 검색 (키워드 LIKE 검색, 페이징)
     * GET /api/mima.wiki/article?keyword=검색어&page=0&size=17
     */
    @GetMapping("/article")
    public ResponseEntity<Page<ArticleRes>> searchArticles(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        Page<ArticleRes> response = articleService.searchArticles(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 글 수정 (작성자 또는 관리자만 직접 수정 가능)
     * PATCH /api/mima.wiki/{keyword}/edit
     */
    @PatchMapping("/{keyword}/edit")
    public ResponseEntity<ArticleRes> editArticle(
            @PathVariable String keyword,
            @RequestBody ArticleReq dto,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        ArticleRes response = articleService.editArticle(keyword, memberId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 글 삭제 (작성자 또는 관리자만 가능)
     * DELETE /api/mima.wiki/{keyword}/delete
     */
    @DeleteMapping("/{keyword}/delete")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable String keyword,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        articleService.deleteArticle(keyword, memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 좋아요 토글
     * GET /api/mima.wiki/w/{keyword}/like
     */
    @GetMapping("/w/{keyword}/like")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable String keyword,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        boolean liked = articleService.toggleLike(keyword, memberId);
        return ResponseEntity.ok(liked);
    }

    /**
     * 실시간 인기 차트 (좋아요 많은 순 10개)
     * GET /api/mima.wiki/chart
     */
    @GetMapping("/chart")
    public ResponseEntity<List<ArticleRes>> getPopularArticles() {
        List<ArticleRes> response = articleService.getPopularArticles();
        return ResponseEntity.ok(response);
    }

    /**
     * JWT에서 memberId 추출
     */
    private Long getMemberIdFromAuth(Authentication authentication) {
        String email = authentication.getName(); // JWT의 subject (email)
        Member member = memberService.getMemberByEmail(email);
        return member.getId();
    }
}