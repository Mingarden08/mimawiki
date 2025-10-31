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

    // 원본 마크다운
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String markdown;

    // 마크다운 -> HTML 변환 결과
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @LastModifiedBy
    @Column(name = "modifiedBy", length = 40)
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "updateTime", updatable = true)
    @ColumnDefault("CURRENT_TIMESTAMP(6)")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ArticleLike> likes = new ArrayList<>();

    public void increaseViewCount() {
        this.viewCount++;
    }
}
