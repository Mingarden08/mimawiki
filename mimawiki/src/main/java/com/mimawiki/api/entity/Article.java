package com.mimawiki.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String markdown;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    // ✅ 추가: 좋아요 개수 캐싱
    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

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

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}