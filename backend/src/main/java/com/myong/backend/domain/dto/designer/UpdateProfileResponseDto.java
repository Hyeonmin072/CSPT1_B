package com.myong.backend.domain.dto.designer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileResponseDto {

    String name;

    String nickname;

    String email;

    String tel;

    String description;

    String image;

    String backgroundImage;
}
