package com.mimawiki.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalResponseLogger implements ResponseBodyAdvice<Object> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalResponseLogger.class);

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 모든 응답을 로깅하려면 true 반환
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, org.springframework.http.server.ServerHttpRequest request,
                                  org.springframework.http.server.ServerHttpResponse response) {

        // 요청 URL 가져오기 (로그에서 어떤 요청에 대한 응답인지 보기 위해)
        String requestUri = "Unknown";
        if (request instanceof ServletServerHttpRequest servletRequest) {
            requestUri = servletRequest.getServletRequest().getRequestURI();
        }

        // 응답 로그 출력
        logger.info("✅ Response [{}]: {}", requestUri, body);

        return body; // 원래 응답을 그대로 반환
    }
}