package com.myong.backend.domain.dto.oauth2;

import com.myong.backend.domain.entity.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SocialSignupRequestDto {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String tel;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private String address;

    @NotNull
    private Integer post;

    @NotNull
    private Gender gender;

}
