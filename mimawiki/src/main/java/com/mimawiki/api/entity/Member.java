package com.mimawiki.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", updatable = false, length = 50, nullable = false, unique = true)
    @Schema(description = "회원 이메일")
    private String email;

    @Column(name = "name", length = 25, nullable = false)
    @Schema(description = "회원이름")
    private String name;

    @Column(name = "passwd", length = 100, nullable = false)
    @Schema(description = "비밀번호")
    private String passwd;

    // ✅ 추가: 권한 필드
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ArticleLike> likes = new ArrayList<>();

    public enum Role {
        USER, ADMIN
    }

    public Member() {

    }
}