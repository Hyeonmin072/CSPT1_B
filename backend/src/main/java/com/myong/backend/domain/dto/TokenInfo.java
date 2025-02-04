package com.myong.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenInfo {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
