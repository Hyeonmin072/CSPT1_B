package com.myong.backend.domain.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGetLocationResponseDto {

    private Double lat;  // 위도
    private Double lng;  // 경도
}
