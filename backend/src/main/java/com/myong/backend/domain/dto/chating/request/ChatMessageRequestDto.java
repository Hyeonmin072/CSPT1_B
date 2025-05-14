package com.myong.backend.domain.dto.chating.request;

import java.time.LocalDateTime;

public record ChatMessageRequestDto (String content, LocalDateTime sendDate){

}
