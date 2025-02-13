package com.myong.backend.domain.dto.event;

import com.myong.backend.domain.entity.user.DiscountType;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

@Value
public class EventListResponseDto implements Serializable {

    @NotBlank
    String name;

    @NotBlank
    Long amount;

    @NotBlank
    DiscountType type;

    @NotBlank
    LocalDate startDate;

    @NotBlank
    LocalDate endDate;
}