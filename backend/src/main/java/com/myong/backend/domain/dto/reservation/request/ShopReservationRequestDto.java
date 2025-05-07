package com.myong.backend.domain.dto.reservation.request;

import com.myong.backend.domain.entity.OrderBy;
import com.myong.backend.domain.entity.Period;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ShopReservationRequestDto {
    @NotNull
    private LocalDate date; // 날짜

    private Period latest; // 추가 검색 기간

    private OrderBy order; // 정렬기준 항목

    private String search; // 검색어
}
