package com.myong.backend.domain.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateLocationRequestDto {

    @NotNull
    private Double lat;  // 위도

    @NotNull
    private Double lng;  // 경도

    @NotNull
    private String address;  // 주소
}
