package com.myong.backend.domain.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

@Value
public class EventRegisterRequestDto implements Serializable {

    @NotBlank
    String name;

    @NotNull
    Long amount;

    @NotBlank
    String type;

    @NotBlank
    String startDate;

    @NotBlank
    String endDate;

    @NotBlank
    String shopEmail;
}