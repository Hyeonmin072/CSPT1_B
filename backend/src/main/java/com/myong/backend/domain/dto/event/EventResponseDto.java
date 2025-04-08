package com.myong.backend.domain.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventResponseDto {

    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotNull
    Integer price;

    @NotBlank
    String type;

    @NotBlank
    String startDate;

    @NotBlank
    String endDate;
}