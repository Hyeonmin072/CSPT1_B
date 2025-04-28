package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.ShopBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopBannerRepository extends JpaRepository<ShopBanner, UUID> {

}
