package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.ArticleReq;
import com.mimawiki.api.dto.res.ArticleRes;
import com.mimawiki.api.dto.res.DataResponse;
import com.mimawiki.api.dto.res.ResponseCode;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.repository.MemberRepository;
import com.mimawiki.api.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Article API", description = "게시글 및 태그 관련 API")
@RestController
@RequestMapping("/api/mima.wiki")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final MemberRepository memberRepository;

    /**
     * 글 작성 (태그 포함)
     * POST /api/mima.wiki/write
     */
    @Operation(summary = "게시글 작성", description = "태그 리스트를 포함하여 게시글을 작성합니다.")
    @PostMapping("/write")
    public ResponseEntity<DataResponse<ArticleRes>> createArticle(
            @RequestBody ArticleReq dto,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            ArticleRes response = articleService.createArticle(memberId, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalStateException e) {
            log.error("게시글 작성 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (IllegalArgumentException e) {
            log.error("게시글 작성 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(DataResponse.of(ResponseCode.NOT_VALID, null));
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "게시글 작성 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 글 조회 (키워드/제목으로)
     * GET /api/mima.wiki/w/{keyword}
     */
    @Operation(summary = "게시글 단건 조회", description = "키워드(제목)로 게시글을 조회합니다.")
    @GetMapping("/w/{keyword}")
    public ResponseEntity<DataResponse<ArticleRes>> getArticle(@PathVariable String keyword) {
        try {
            ArticleRes response = articleService.getArticleByKeyword(keyword);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalArgumentException e) {
            log.error("게시글 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.of(ResponseCode.NOT_FOUND_GALLERY, null));
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "게시글 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * ✅ [New URL] 해시태그로 게시글 목록 검색
     * GET /api/mima.wiki/w/tag/{keyword}
     * (기존 /tags/{tagName} 대체)
     */
    @Operation(summary = "해시태그 검색", description = "특정 태그가 달린 게시글 목록을 조회합니다.")
    @GetMapping("/w/tag/{keyword}")
    public ResponseEntity<DataResponse<Page<ArticleRes>>> searchByTag(
            @PathVariable("keyword") String tagName,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<ArticleRes> response = articleService.searchArticlesByTag(tagName, pageable);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (Exception e) {
            log.error("태그 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "태그 검색 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 글 수정 (태그 수정 포함)
     * PUT /api/mima.wiki/w/{keyword}
     */
    @Operation(summary = "게시글 수정", description = "내용 및 태그를 수정합니다.")
    @PutMapping("/w/{keyword}")
    public ResponseEntity<DataResponse<ArticleRes>> editArticle(
            @PathVariable String keyword,
            @RequestBody ArticleReq dto,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            ArticleRes response = articleService.editArticle(keyword, memberId, dto);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (IllegalStateException e) {
            log.error("게시글 수정 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (IllegalArgumentException e) {
            log.error("게시글 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.of(ResponseCode.NOT_FOUND_GALLERY, null));
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "게시글 수정 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 글 삭제
     * DELETE /api/mima.wiki/w/{keyword}
     */
    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/w/{keyword}")
    public ResponseEntity<DataResponse<Void>> deleteArticle(
            @PathVariable String keyword,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            articleService.deleteArticle(keyword, memberId);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
        } catch (IllegalStateException e) {
            log.error("게시글 삭제 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN));
        } catch (IllegalArgumentException e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.of(ResponseCode.NOT_FOUND_GALLERY));
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "게시글 삭제 중 오류가 발생했습니다."));
        }
    }

    /**
     * 일반 검색 (제목/내용 포함)
     * GET /api/mima.wiki/search?keyword=...
     */
    @Operation(summary = "게시글 검색", description = "제목 또는 내용에 키워드가 포함된 글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<DataResponse<Page<ArticleRes>>> searchArticles(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<ArticleRes> response = articleService.searchArticles(keyword, pageable);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (Exception e) {
            log.error("게시글 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "게시글 검색 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 좋아요 토글
     * GET /api/mima.wiki/w/{keyword}/like
     */
    @Operation(summary = "좋아요 토글")
    @GetMapping("/w/{keyword}/like")
    public ResponseEntity<DataResponse<Boolean>> toggleLike(
            @PathVariable String keyword,
            Authentication authentication) {
        try {
            Long memberId = getMemberIdFromAuth(authentication);
            boolean liked = articleService.toggleLike(keyword, memberId);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, liked));
        } catch (IllegalStateException e) {
            log.error("좋아요 토글 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DataResponse.of(ResponseCode.FORBIDDEN, null));
        } catch (IllegalArgumentException e) {
            log.error("좋아요 토글 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.of(ResponseCode.NOT_FOUND_GALLERY, null));
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "좋아요 처리 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 실시간 인기 차트
     * GET /api/mima.wiki/chart
     */
    @Operation(summary = "인기글 조회")
    @GetMapping("/chart")
    public ResponseEntity<DataResponse<List<ArticleRes>>> getPopularArticles() {
        try {
            List<ArticleRes> response = articleService.getPopularArticles();
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
        } catch (Exception e) {
            log.error("인기글 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.of(500, "인기글 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 전체 게시글 목록 조회
     * GET /api/mima.wiki/article?page=0
     */
    @Operation(summary = "전체 게시글 조회", description = "모든 게시글을 페이징하여 조회합니다.")
    @GetMapping("/article")
    public ResponseEntity<Page<ArticleRes>> getAllArticles(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ArticleRes> response = articleService.getAllArticles(pageable);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Private Helper Methods
    // ============================================================

    private Long getMemberIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return member.getId();
    }
}