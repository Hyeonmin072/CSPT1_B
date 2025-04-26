package com.myong.backend.repository;

import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    ArrayList<Payment> findByUser(User user);

    Optional<Payment> findByReservationId(UUID reservationId);
}
