package com.myong.backend.domain.dto.designer;

import jakarta.mail.Multipart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequestDto {

    private String updateNickName;  //닉네임

    private String updateDesc; // 소개글

    private String updateTel; // 연락처

    private MultipartFile updateImage; // 사진

    private MultipartFile updateBackgroundImage;

    private String oldPwd;

    private String newPwd;

    private String checkPwd;

}
