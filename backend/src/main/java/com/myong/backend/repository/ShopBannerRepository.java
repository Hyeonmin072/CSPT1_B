package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.shop.ShopBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShopBannerRepository extends JpaRepository<ShopBanner, UUID> {

    Optional<ShopBanner> findByImage(String url);

    ShopBanner shop(Shop shop);
}
