package com.myong.backend.domain.dto.reservation;

import com.myong.backend.domain.entity.business.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ShopReservationDetailResponseDto {

    @NotBlank
    private String menuName; // 예약한 메뉴 이름

    @NotNull
    private LocalDateTime serviceDate; // 예약해놓은 날짜

    @NotBlank
    private String userName; // 예약한 유저 이름

    @NotBlank
    private String designerName; // 예약받은 디자이너 이름

    @NotBlank
    private PaymentMethod payMethod; // 결제방법

    @NotNull
    private Integer menuPrice; // 금액
}
