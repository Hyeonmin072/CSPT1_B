package com.myong.backend.domain.dto.designer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DesignerEmailRequestDto {
    @NotBlank
    @Email
    String email; // 디자이너 이메일
}
