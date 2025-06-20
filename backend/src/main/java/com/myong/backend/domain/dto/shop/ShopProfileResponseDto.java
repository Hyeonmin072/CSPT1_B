package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.designer.RegularHoliday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ShopProfileResponseDto {

    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotNull
    Integer post;

    @NotBlank
    String tel;

    @NotBlank
    String pwd;

    @NotNull
    Integer reservationNumber; // 총 예약 수

    @NotNull
    Integer reviewNumber; // 총 리뷰 수

    @NotNull
    LocalDate joinDate; // 회원가입일

    @NotNull
    Double rating; // 평점

    String desc;

    String open;

    String close;

    RegularHoliday regularHoliday;

    String thumbnail;

    List<String> bannerImages;
}
