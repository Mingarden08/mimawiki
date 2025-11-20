package com.mimawiki.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", updatable = false, length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 25, nullable = false)
    private String name;

    @Column(name = "passwd", length = 100, nullable = false)
    private String passwd;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    // ✅ [수정 1] 작성한 글 목록 매핑 (Member : Article = 1 : N)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Article> articles = new ArrayList<>();

    // ✅ [수정 2] Profile 매핑 (Member : Profile = 1 : 1)
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Profile profile;

    // ✅ [수정 3] 이름 수정 비즈니스 로직
    public void updateName(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
    }

    public enum Role {
        USER, ADMIN
    }
}