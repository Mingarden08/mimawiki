package com.mimawiki.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_edit_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleEditRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String markdown;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 500)
    private String requestComment; // 수정 요청 사유

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Member reviewer; // 승인/거절한 사람

    private LocalDateTime reviewedAt;

    @Column(length = 500)
    private String reviewComment; // 승인/거절 사유

    public enum RequestStatus {
        PENDING,    // 대기중
        APPROVED,   // 승인됨
        REJECTED    // 거절됨
    }
}