package com.myong.backend.domain.entity.shop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ShopBanner {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    @Column(name = "sb_id")
    private UUID id;

    @Column(name = "sb_image")
    private String image;    // 이미지 url

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop;       // 가게 배너


    public static ShopBanner save (String image, Shop shop){
        return ShopBanner.builder()
                .image(image)
                .shop(shop)
                .build();
    }
}
