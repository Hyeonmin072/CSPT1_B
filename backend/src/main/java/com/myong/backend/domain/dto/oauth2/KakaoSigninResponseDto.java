package com.myong.backend.domain.dto.oauth2;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoSigninResponseDto {


    private String email;
    private String nickname;
    private String status;
}
