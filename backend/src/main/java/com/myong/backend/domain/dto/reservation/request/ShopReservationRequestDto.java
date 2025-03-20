package com.myong.backend.domain.dto.reservation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ShopReservationRequestDto {

    @NotBlank
    private String shopEmail; // 가게 이메일

    @NotNull
    private LocalDate date; // 날짜

    private Period latest; // 추가 검색 기간

    private OrderBy order; // 정렬기준 항목

    private String search; // 검색어

    public enum Period {
        ONE_WEEK, ONE_MONTH, ONE_YEAR // 최근 1주일, 1달, 1년
    }

    public enum OrderBy {
        TIME, CUSTOMER_NAME, DESIGNER_NAME, PRICE, PAYMENT_STATUS // 일시순, 고객이름순, 디자이너이름순, 결제상태순

    }
}
