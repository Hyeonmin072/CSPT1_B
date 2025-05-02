package com.myong.backend.domain.dto.Alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDto {
    private String title; //제목
    private String content; //내용
    private String jwtToken;//유저아이디와 역할 추출용
}
