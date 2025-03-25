package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Event;
import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByShop(Shop shop);

    void deleteByEndDateBefore(LocalDate endDateBefore);
}
