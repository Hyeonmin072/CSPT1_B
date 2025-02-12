package com.myong.backend.domain.dto.event;

import lombok.Value;

import java.io.Serializable;

@Value
public class EventListRequestDto implements Serializable {
    String email;
}