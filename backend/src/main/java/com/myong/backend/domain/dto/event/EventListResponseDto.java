package com.myong.backend.domain.dto.event;

import com.myong.backend.domain.entity.user.DiscountType;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

@Value
public class EventListResponseDto implements Serializable {
    String name;
    Long amount;
    DiscountType type;
    LocalDate startDate;
    LocalDate endDate;
}