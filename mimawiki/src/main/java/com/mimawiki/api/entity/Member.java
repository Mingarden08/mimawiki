package com.mimawiki.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TBL_MEMBER")
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
    @Schema(description = "비빌번호")
    private String passwd;

    public Member() {

    }

    @PrePersist
    public void prePersist() {
        //사전 초기화 작업이 필요한 참조형 변수가 있다면
        //여기에서 초기화.
    }


    //updateEntity from Dto  or from String(이름...)

    public void updateEntity(String name) {
        this.setName(name);
    }

}
