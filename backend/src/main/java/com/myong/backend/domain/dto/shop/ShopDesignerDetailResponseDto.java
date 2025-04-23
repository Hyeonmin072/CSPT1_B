package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.designer.RegularHoliday;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;

@Value
@Builder
public class ShopDesignerDetailResponseDto {
    
    @NotBlank
    String name; // 디자이너 이름

    @NotBlank
    @Email
    String email;// 디자이너 이메일

    @NotBlank
    String gender;// 디자이너 성별

    @NotNull
    Integer like; // 디자이너 좋아요 개수

    String image; // 디자이너 이미지

    String tel; // 디자이너 전화번호

    LocalTime workTime; // 디자이너 정시 출근 시간

    LocalTime leaveTime; // 디자이너 정시 퇴근 시간

    RegularHoliday regularHoliday; // 디자이너 정기 휴무일
}
