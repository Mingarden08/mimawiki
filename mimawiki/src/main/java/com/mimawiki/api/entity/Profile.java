package com.mimawiki.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Member와 1:1 필수 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(length = 100)
    private String nickname;

    @Column(length = 500)
    private String bio;

    @Column(length = 255)
    private String profileImageUrl;

    @LastModifiedDate
    @Column(name = "updateTime", updatable = true)
    @ColumnDefault("CURRENT_TIMESTAMP(6)")
    private LocalDateTime updateTime;

    // 추후 프로필 정보 수정 시 사용할 편의 메서드
    public void updateInfo(String bio, String nickname) {
        this.bio = bio;
        this.nickname = nickname;
    }
}