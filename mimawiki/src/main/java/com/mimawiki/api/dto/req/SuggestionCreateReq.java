package com.mimawiki.api.dto.req;

import com.mimawiki.api.entity.Suggestion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

// 제안 생성 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionCreateReq {

    @Schema(description = "제안 내용", example = "이 부분을 이렇게 수정하면 더 좋을 것 같습니다.")
    @NotBlank(message = "제안 내용은 필수입니다.")
    @Size(max = 1000, message = "제안 내용은 1000자를 초과할 수 없습니다.")
    private String content;
}