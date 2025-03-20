package com.myong.backend.domain.dto.designer;


import com.myong.backend.annotation.PhoneNumber;
import com.myong.backend.domain.entity.Gender;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SignUpRequestDto {


    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;//비밀번호 확인용

    @NotBlank
    @PhoneNumber
    private String tel;

    @NotBlank
    private String birth;

    @NotNull
    private Gender gender;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    private boolean isPwdMatching(){
        return password.equals(confirmPassword);
    };
}
