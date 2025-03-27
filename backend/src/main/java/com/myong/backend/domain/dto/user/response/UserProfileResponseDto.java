package com.myong.backend.domain.dto.user.response;


import com.myong.backend.domain.entity.user.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {

    private String userName; // 유저이름
    private String userEmail; // 유저 이메일
    private String userAdress; // 유저 현 거주지
    private String userTel; // 유저 전화번호
    private Grade userGrade; // 유저 등급

}
