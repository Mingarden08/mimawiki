package com.mimawiki.api.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateReq {
    @Schema(description = "변경할 사용자 이름", example = "새로운이름")
    private String name;
}