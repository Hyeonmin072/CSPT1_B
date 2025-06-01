package com.myong.backend.domain.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class EventRequestDto implements Serializable {

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