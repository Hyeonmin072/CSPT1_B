package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Notice;
import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, UUID> {
    List<Notice> findByShop(Shop shop);
}
