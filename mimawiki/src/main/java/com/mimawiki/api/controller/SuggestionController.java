package com.mimawiki.api.controller;

import com.mimawiki.api.dto.res.DataResponse;
import com.mimawiki.api.dto.req.SuggestionCreateReq;
import com.mimawiki.api.dto.res.SuggestionRes;
import com.mimawiki.api.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mima.wiki")
@RequiredArgsConstructor
@Tag(name = "Suggestion API", description = "게시글 수정 제안 관련 API")
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping("/w/{keyword}/suggestions")
    @Operation(
            summary = "제안 작성",
            description = "특정 게시글에 수정 제안을 작성합니다."
    )
    public ResponseEntity<DataResponse<SuggestionRes>> createSuggestion(
            @PathVariable String keyword,
            @Valid @RequestBody SuggestionCreateReq request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuggestionRes result = suggestionService.createSuggestion(
                keyword,
                request,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(DataResponse.of(result));
    }

    @GetMapping("/w/{keyword}/suggestions")
    @Operation(
            summary = "게시글의 제안 목록 조회",
            description = "특정 게시글에 달린 모든 제안을 조회합니다."
    )
    public ResponseEntity<DataResponse<Page<SuggestionRes>>> getSuggestionsByArticle(
            @PathVariable String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<SuggestionRes> result = suggestionService.getSuggestionsByArticle(keyword, pageable);
        return ResponseEntity.ok(DataResponse.of(result));
    }

    @GetMapping("/suggestions/received")
    @Operation(
            summary = "내 게시글에 달린 제안 목록",
            description = "내 게시글에 다른 사람들이 작성한 제안 목록을 조회합니다."
    )
    public ResponseEntity<DataResponse<Page<SuggestionRes>>> getReceivedSuggestions(
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Page<SuggestionRes> result = suggestionService.getReceivedSuggestions(
                userDetails.getUsername(),
                pageable
        );
        return ResponseEntity.ok(DataResponse.of(result));
    }

    @GetMapping("/suggestions/unread-count")
    @Operation(
            summary = "읽지 않은 제안 개수",
            description = "내 게시글에 달린 읽지 않은 제안의 개수를 조회합니다."
    )
    public ResponseEntity<DataResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        long count = suggestionService.getUnreadSuggestionCount(userDetails.getUsername());
        return ResponseEntity.ok(DataResponse.of(count));
    }

    @PatchMapping("/suggestions/{suggestionId}/read")
    @Operation(
            summary = "제안 읽음 처리",
            description = "제안을 읽음 상태로 변경합니다. (게시글 작성자만 가능)"
    )
    public ResponseEntity<DataResponse<SuggestionRes>> markAsRead(
            @PathVariable Long suggestionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuggestionRes result = suggestionService.markAsRead(
                suggestionId,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(DataResponse.of(result));
    }

    @DeleteMapping("/suggestions/{suggestionId}")
    @Operation(
            summary = "제안 삭제",
            description = "내가 작성한 제안을 삭제합니다."
    )
    public ResponseEntity<DataResponse<Void>> deleteSuggestion(
            @PathVariable Long suggestionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        suggestionService.deleteSuggestion(suggestionId, userDetails.getUsername());
        return ResponseEntity.ok(DataResponse.of(null));
    }
}