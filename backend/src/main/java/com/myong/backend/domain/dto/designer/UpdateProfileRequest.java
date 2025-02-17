package com.myong.backend.domain.dto.designer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class UpdateProfileRequest {

    private String updateNickName;  //닉네임

    private String updateDesc; // 소개글

    private String updateTel; // 연락처

    private String updateImage; // 사진

    private String oldPwd;

    private String newPwd;

    private String checkPwd;

}
