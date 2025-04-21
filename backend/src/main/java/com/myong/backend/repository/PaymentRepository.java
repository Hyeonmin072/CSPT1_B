package com.myong.backend.repository;

import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByReservation(Reservation reservation);

    Optional<Payment> findByPaymentKeyAndUser_Email(String paymentKey, String userEmail);

    Slice<Payment> findAllByUser_Email(String email, Pageable pageable);
}
