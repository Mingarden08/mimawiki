package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.LoginReq;
import com.mimawiki.api.dto.res.DataResponse;
import com.mimawiki.api.dto.res.LoginResultRes;
import com.mimawiki.api.dto.res.ResponseCode;
import com.mimawiki.api.service.AuthService;
import com.mimawiki.api.service.TokenBlacklistService;
import com.mimawiki.api.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/mima.wiki/auth")
@Tag(name = "Auth API", description = "인증/로그인 관련 API")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    // ✅ 추가: 블랙리스트 서비스
    private final TokenBlacklistService tokenBlacklistService;

    // ✅ 생성자 수정
    public AuthController(JwtUtil jwtUtil, AuthService authService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<DataResponse<LoginResultRes>> login(@RequestBody LoginReq req) {
        LoginResultRes res = new LoginResultRes();
        try {
            String token = this.authService.login(req);
            res.setToken(token);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, res));
        } catch (RuntimeException e) {
            // 예시: 비밀번호 불일치 등 에러 처리
            return ResponseEntity.ok(DataResponse.of(ResponseCode.NOT_MATCHED, null));
        }
    }

    /**
     * ✅ [New] 로그아웃
     * 토큰을 블랙리스트에 등록하여 무효화합니다.
     */
    @Operation(summary = "로그아웃", description = "해당 토큰을 서버 블랙리스트에 등록하여 더 이상 사용할 수 없게 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<DataResponse<Void>> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
        String token = header.substring(7);

        // 토큰 만료 시간 가져오기 (JwtUtil에 getExpiration 메서드 필요)
        Date expirationDate = jwtUtil.getExpiration(token);
        long now = System.currentTimeMillis();

        // 만료 시간이 남은 토큰만 블랙리스트에 등록
        if (expirationDate.getTime() > now) {
            tokenBlacklistService.blacklistToken(token, expirationDate.getTime());
        }

        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
    }
}