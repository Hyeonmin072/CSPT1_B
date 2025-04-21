package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.shop.Notice;
import com.myong.backend.domain.entity.shop.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomePageResponseDto {
    private String name; //디자이너 이름

    private String tel; //디자이너 전화번호

    private Shop shop; //소속된 가게

    private List<Reservation> reservations; //당일 예약

    private List<Notice> notices; // 공지사항

    private List<Notice> importNotices; //중요공지
}
