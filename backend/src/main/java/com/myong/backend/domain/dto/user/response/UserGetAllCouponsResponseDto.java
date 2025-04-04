package com.myong.backend.domain.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGetAllCouponsResponseDto {

    private Integer price;    // 할인가격
    private String shopName; // 가게이름
    private LocalDate expireDate; // 쿠폰만료기간

}
