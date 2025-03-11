package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    List<Coupon> findByShop(Shop shop);

    Optional<Coupon> findById(UUID uuid);
}
