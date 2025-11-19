package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.ArticleEditRequestReq;
import com.mimawiki.api.dto.req.ReviewEditRequestReq;
import com.mimawiki.api.dto.res.ArticleEditRequestRes;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.service.ArticleEditRequestService;
import com.mimawiki.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mima.wiki")
@RequiredArgsConstructor
public class ArticleEditRequestController {

    private final ArticleEditRequestService editRequestService;
    private final MemberService memberService;

    /**
     * 수정 요청 생성
     * POST /api/mima.wiki/w/{keyword}/edit-request
     */
    @PostMapping("/w/{keyword}/edit-request")
    public ResponseEntity<ArticleEditRequestRes> createEditRequest(
            @PathVariable String keyword,
            @RequestBody ArticleEditRequestReq dto,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        ArticleEditRequestRes response = editRequestService.createEditRequest(keyword, memberId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 수정 요청 승인/거절
     * PATCH /api/mima.wiki/edit-requests/{requestId}/review
     */
    @PatchMapping("/edit-requests/{requestId}/review")
    public ResponseEntity<ArticleEditRequestRes> reviewEditRequest(
            @PathVariable Long requestId,
            @RequestBody ReviewEditRequestReq dto,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        ArticleEditRequestRes response = editRequestService.reviewEditRequest(requestId, memberId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 받은 수정 요청 조회 (내 글에 대한 수정 요청)
     * GET /api/mima.wiki/my-article-edit-requests
     */
    @GetMapping("/my-article-edit-requests")
    public ResponseEntity<Page<ArticleEditRequestRes>> getMyArticleEditRequests(
            Pageable pageable,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        Page<ArticleEditRequestRes> response = editRequestService.getMyArticleEditRequests(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 요청한 수정 요청 조회
     * GET /api/mima.wiki/my-edit-requests
     */
    @GetMapping("/my-edit-requests")
    public ResponseEntity<Page<ArticleEditRequestRes>> getMyEditRequests(
            Pageable pageable,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        Page<ArticleEditRequestRes> response = editRequestService.getMyEditRequests(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글의 수정 요청 조회
     * GET /api/mima.wiki/w/{keyword}/edit-requests
     */
    @GetMapping("/w/{keyword}/edit-requests")
    public ResponseEntity<Page<ArticleEditRequestRes>> getArticleEditRequests(
            @PathVariable String keyword,
            Pageable pageable) {

        Page<ArticleEditRequestRes> response = editRequestService.getArticleEditRequests(keyword, pageable);
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