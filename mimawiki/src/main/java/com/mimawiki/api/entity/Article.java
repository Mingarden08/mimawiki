package com.mimawiki.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String keyword;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @CreatedBy
    @Column(name = "createdBy", length = 40, nullable = true, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "modifiedBy", length = 40, nullable = true, updatable = true)
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "updateTime", updatable = true)
    @ColumnDefault("CURRENT_TIMESTAMP(6)")
    private LocalDateTime updateTime;

    // 이 글에 달린 좋아요 목록
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ArticleLike> likes = new ArrayList<>();

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }
}
