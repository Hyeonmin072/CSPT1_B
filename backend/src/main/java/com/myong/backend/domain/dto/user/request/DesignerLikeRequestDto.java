package com.myong.backend.domain.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DesignerLikeRequestDto {
    @NotNull
    @Email
    private String designerEmail;
}
