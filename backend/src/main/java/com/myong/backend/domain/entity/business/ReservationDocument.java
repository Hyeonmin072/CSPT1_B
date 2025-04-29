package com.myong.backend.domain.entity.business;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Document(indexName = "reservation")
public class ReservationDocument {

    @Id
    private UUID id;

    private LocalDateTime createDate; // 예약 접수 날짜

    private LocalDateTime serviceDate; // 서비스 받을 날짜

    private Integer price;  // 결제금액

    private String menu;    // 메뉴 이름

    private String shop;    // 가게 이름

    private String designer;// 디자이너 이름

    private String user;    // 유저 이름

    public static ReservationDocument from(Reservation reservation){
        return ReservationDocument.builder()
                .id(reservation.getId())
                .createDate(reservation.getCreateDate())
                .serviceDate(reservation.getServiceDate())
                .price(reservation.getPrice())
                .menu(reservation.getMenu().getName())
                .shop(reservation.getShop().getName())
                .designer(reservation.getDesigner().getName())
                .user(reservation.getUser().getName())
                .build();
    }

}
