package com.myong.backend.domain.dto.user;

import com.myong.backend.domain.entity.Gender;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignUpDto {

    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String tel;
    @NotNull
    private LocalDate birth;
    @NotNull
    private Gender gender;
    @NotBlank
    private String address;
    @NotNull
    private Integer post;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    private boolean isPasswordMatching(){
        return password.equals(confirmPassword);
    }
}
