package com.myong.backend.repository;

import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findById(UUID id);

    List<Reservation> findByShop(Shop shop);
}
