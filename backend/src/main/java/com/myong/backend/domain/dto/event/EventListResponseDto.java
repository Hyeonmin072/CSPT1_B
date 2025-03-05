package com.myong.backend.domain.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

@Value
public class EventListResponseDto implements Serializable {

    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    Long amount;

    @NotBlank
    String type;

    @NotBlank
    String startDate;

    @NotBlank
    String endDate;
}