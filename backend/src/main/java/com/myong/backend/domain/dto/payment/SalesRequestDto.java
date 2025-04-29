package com.myong.backend.domain.dto.payment;

import com.myong.backend.domain.entity.Period;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesRequestDto {
    @NotNull
    private Period latest; // 추가 검색 기간
}
