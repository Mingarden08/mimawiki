package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.ArticleEditRequestReq;
import com.mimawiki.api.dto.req.ReviewEditRequestReq;
import com.mimawiki.api.dto.res.ArticleEditRequestRes;
import com.mimawiki.api.dto.res.DataResponse;
import com.mimawiki.api.dto.res.ResponseCode;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.service.ArticleEditRequestService;
import com.mimawiki.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    public ResponseEntity<DataResponse<ArticleEditRequestRes>> createEditRequest(
            @PathVariable String keyword,
            @RequestBody ArticleEditRequestReq dto,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            ArticleEditRequestRes response = editRequestService.createEditRequest(keyword, memberId, dto);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalStateException e) {
            log.error("수정 요청 생성 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (IllegalArgumentException e) {
            log.error("수정 요청 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(DataResponse.of(ResponseCode.NOT_VALID, null));
        } catch (Exception e) {
            log.error("수정 요청 생성 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "수정 요청 생성 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 수정 요청 승인/거절
     * PATCH /api/mima.wiki/edit-requests/{requestId}/review
     */
    @PatchMapping("/edit-requests/{requestId}/review")
    public ResponseEntity<DataResponse<ArticleEditRequestRes>> reviewEditRequest(
            @PathVariable Long requestId,
            @RequestBody ReviewEditRequestReq dto,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            ArticleEditRequestRes response = editRequestService.reviewEditRequest(requestId, memberId, dto);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalStateException e) {
            log.error("수정 요청 검토 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (IllegalArgumentException e) {
            log.error("수정 요청 검토 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.of(ResponseCode.NOT_FOUND_GALLERY, null));
        } catch (Exception e) {
            log.error("수정 요청 검토 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "수정 요청 검토 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 내가 받은 수정 요청 조회 (내 글에 대한 수정 요청)
     * GET /api/mima.wiki/my-article-edit-requests
     */
    @GetMapping("/my-article-edit-requests")
    public ResponseEntity<DataResponse<Page<ArticleEditRequestRes>>> getMyArticleEditRequests(
            Pageable pageable,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            Page<ArticleEditRequestRes> response = editRequestService.getMyArticleEditRequests(memberId, pageable);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalStateException e) {
            log.error("내 글 수정 요청 조회 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (Exception e) {
            log.error("내 글 수정 요청 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "수정 요청 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 내가 요청한 수정 요청 조회
     * GET /api/mima.wiki/my-edit-requests
     */
    @GetMapping("/my-edit-requests")
    public ResponseEntity<DataResponse<Page<ArticleEditRequestRes>>> getMyEditRequests(
            Pageable pageable,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            Page<ArticleEditRequestRes> response = editRequestService.getMyEditRequests(memberId, pageable);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalStateException e) {
            log.error("내가 요청한 수정 요청 조회 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (Exception e) {
            log.error("내가 요청한 수정 요청 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "수정 요청 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 특정 게시글의 수정 요청 조회
     * GET /api/mima.wiki/w/{keyword}/edit-requests
     */
    @GetMapping("/w/{keyword}/edit-requests")
    public ResponseEntity<DataResponse<Page<ArticleEditRequestRes>>> getArticleEditRequests(
            @PathVariable String keyword,
            Pageable pageable) {
        try {
            Page<ArticleEditRequestRes> response = editRequestService.getArticleEditRequests(keyword, pageable);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalArgumentException e) {
            log.error("게시글 수정 요청 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.of(ResponseCode.NOT_FOUND_GALLERY, null));
        } catch (Exception e) {
            log.error("게시글 수정 요청 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "수정 요청 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * JWT에서 memberId 추출
     */
    private Long getMemberIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        String email = authentication.getName(); // JWT의 subject (email)
        Member member = memberService.getMemberByEmail(email);
        return member.getId();
    }
}