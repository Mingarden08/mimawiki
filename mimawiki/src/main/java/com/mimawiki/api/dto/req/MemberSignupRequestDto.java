package com.mimawiki.api.dto.req;

import com.mimawiki.api.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class MemberSignupRequestDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "이메일 형식이 올바르지 않습니다."
    )
    @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*?_]{8,50}$",
            message = "비밀번호는 영문자, 숫자, 특수문자(!@#$%^&*?_)만 포함하여 8~50자 사이여야 합니다."
    )
    private String passwd;


    public Member toEntity() {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .passwd(this.passwd)
                .build();
    }
}
