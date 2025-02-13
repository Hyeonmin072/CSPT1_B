package com.myong.backend.domain.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignUpDto {

    private String name;
    private String email;
    private String password;
    private String tel;
    private LocalDate birth;
    private String gender;
    private String address;
}
