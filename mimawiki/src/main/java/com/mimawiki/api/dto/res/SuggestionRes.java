package com.mimawiki.api.dto.res;

import com.mimawiki.api.entity.Suggestion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionRes {

    @Schema(description = "제안 ID")
    private Long id;

    @Schema(description = "게시글 키워드")
    private String articleKeyword;

    @Schema(description = "게시글 제목")
    private String articleTitle;

    @Schema(description = "제안자 이름")
    private String suggesterName;

    @Schema(description = "제안 내용")
    private String content;

    @Schema(description = "읽음 여부")
    private Boolean isRead;

    @Schema(description = "제안 시간")
    private LocalDateTime regTime;

    public static SuggestionRes from(Suggestion suggestion) {
        return SuggestionRes.builder()
                .id(suggestion.getId())
                .articleKeyword(suggestion.getArticle().getKeyword())
                .articleTitle(suggestion.getArticle().getKeyword())
                .suggesterName(suggestion.getMember().getName())
                .content(suggestion.getContent())
                .isRead(suggestion.getIsRead())
                .regTime(suggestion.getRegTime())
                .build();
    }
}