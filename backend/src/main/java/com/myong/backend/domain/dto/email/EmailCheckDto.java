package com.myong.backend.domain.dto.email;

import lombok.Data;

@Data
public class EmailCheckDto {

    private String email;
    private String authNum;
}
