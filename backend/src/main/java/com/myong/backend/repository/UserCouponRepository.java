package com.myong.backend.repository;

import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.user.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserCouponRepository extends JpaRepository<UserCoupon, UUID> {

    List<UserCoupon> findAllByUser(User user);
}
