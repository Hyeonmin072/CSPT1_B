package com.myong.backend.domain.dto.event;

import lombok.Value;

import java.io.Serializable;

@Value
public class EventRegisterRequestDto implements Serializable {
    String name;
    Long amount;
    String type;
    String startDate;
    String endDate;
    String shopEmail;
}