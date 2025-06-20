package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class ShopBizRequestDto {

    @NotBlank
    @Length(min = 10, max = 10, message = "사업자번호는 정확히 10자리여야 합니다.")
    String bizId;

    @NotBlank
    String tel;
}
