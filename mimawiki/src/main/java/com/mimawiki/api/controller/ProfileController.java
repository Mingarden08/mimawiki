package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.ProfileUpdateReq;
import com.mimawiki.api.dto.res.DataResponse;
import com.mimawiki.api.dto.res.ProfileRes;
import com.mimawiki.api.dto.res.ResponseCode;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.repository.MemberRepository;
import com.mimawiki.api.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile API", description = "마이페이지/프로필 관련 API")
@RestController
@RequestMapping("/api/mima.wiki/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MemberRepository memberRepository;

    /**
     * 내 프로필 조회
     * GET /api/mima.wiki/profile/me
     * * Body 없이 그냥 호출하면:
     * 1. page=0 (첫 페이지)
     * 2. size=7 (7개씩)
     * 3. sort=id, DESC (최신순)
     * 으로 자동 설정되어 결과를 반환합니다.
     */
    @Operation(summary = "내 프로필 조회", description = "내 정보와 작성한 글 목록(최신순 7개)을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<DataResponse<ProfileRes>> getMyProfile(
            Authentication authentication,
            // ✅ [핵심 수정] size를 7로 변경, sort를 id 역순(최신순)으로 고정
            @PageableDefault(size = 7, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Long memberId = getMemberIdFromAuth(authentication);

        // 서비스는 그대로 호출하면 알아서 7개씩 잘라서 가져옵니다.
        ProfileRes response = profileService.getProfile(memberId, pageable);

        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));

    }

    /**
     * 내 프로필 수정 (이름)
     * PUT /api/mima.wiki/profile/me
     */
    @Operation(summary = "프로필 수정", description = "사용자 이름을 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<DataResponse<String>> updateMyProfile(
            @RequestBody ProfileUpdateReq req,
            Authentication authentication) {

        Long memberId = getMemberIdFromAuth(authentication);
        profileService.updateProfile(memberId, req);
z
        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
    }

    // Helper Method
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