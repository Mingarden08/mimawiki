package com.mimawiki.api.dto.res;

import com.mimawiki.api.entity.ArticleEditRequest;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleEditRequestRes {
    private Long id;
    private Long articleId;
    private String articleKeyword;
    private Long requesterId;
    private String requesterName;
    private String markdown;
    private String content;
    private String requestComment;
    private String status;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime reviewedAt;
    private String reviewComment;
    private LocalDateTime createdAt;

    public static ArticleEditRequestRes fromEntity(ArticleEditRequest entity) {
        return ArticleEditRequestRes.builder()
                .id(entity.getId())
                .articleId(entity.getArticle().getId())
                .articleKeyword(entity.getArticle().getKeyword())
                .requesterId(entity.getRequester().getId())
                .requesterName(entity.getRequester().getName())
                .markdown(entity.getMarkdown())
                .content(entity.getContent())
                .requestComment(entity.getRequestComment())
                .status(entity.getStatus().name())
                .reviewerId(entity.getReviewer() != null ? entity.getReviewer().getId() : null)
                .reviewerName(entity.getReviewer() != null ? entity.getReviewer().getName() : null)
                .reviewedAt(entity.getReviewedAt())
                .reviewComment(entity.getReviewComment())
                .createdAt(entity.getRegTime())
                .build();
    }
}