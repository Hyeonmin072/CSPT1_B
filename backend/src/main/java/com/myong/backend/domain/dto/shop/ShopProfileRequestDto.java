package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.designer.RegularHoliday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopProfileRequestDto {
    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotNull
    Integer post;

    @NotBlank
    String tel;

    String newPwd;

    String newPwdConfirm;

    String desc;

    String open;

    String close;

    @NotNull
    RegularHoliday regularHoliday;

}
