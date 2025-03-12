package com.myong.backend.repository;

import com.myong.backend.domain.entity.business.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findById(UUID id);

}
