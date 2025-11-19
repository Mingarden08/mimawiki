package com.mimawiki.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
// ✅ 추가: unique 제약조건
@Table(name = "article_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_article_member",
                        columnNames = {"article_id", "member_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
