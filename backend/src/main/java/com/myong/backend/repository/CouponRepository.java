package com.myong.backend.repository;

import com.myong.backend.domain.dto.coupon.CouponListResponseDto;
import com.myong.backend.domain.entity.user.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query("select new com.myong.backend.domain.dto.coupon.CouponListResponseDto(c.name, c.type, c.amount, c.getDate, c.useDate) " +
            "from Coupon c join c.shop s " +
            "where s.id = :shopId")
    List<CouponListResponseDto> findByShop(@Param("shopId") UUID uuid);
}
