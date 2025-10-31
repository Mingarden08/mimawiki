package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.LoginReq;
import com.mimawiki.api.dto.res.DataResponse;
import com.mimawiki.api.dto.res.LoginResultRes;
import com.mimawiki.api.dto.res.ResponseCode;
import com.mimawiki.api.service.AuthService;
import com.mimawiki.api.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mima.wiki/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<LoginResultRes>> login(@RequestBody LoginReq req) {
        LoginResultRes res = new LoginResultRes();
        try {
            String token = authService.login(req);
            res.setToken(token);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, res));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(DataResponse.of(ResponseCode.NOT_MATCHED, res));
        }

    }
}
