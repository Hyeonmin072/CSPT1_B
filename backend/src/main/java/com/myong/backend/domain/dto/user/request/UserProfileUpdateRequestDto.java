package com.myong.backend.domain.dto.user.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserProfileUpdateRequestDto {

    @NotNull
    @NotEmpty
    public String address;
}
