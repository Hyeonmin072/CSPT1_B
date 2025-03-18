package com.myong.backend.domain.dto.review;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRemoveRequestDto {
    @NotBlank
    private String reviewId;
}
