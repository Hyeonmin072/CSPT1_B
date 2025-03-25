package com.myong.backend.repository;

import com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findById(UUID id);

    List<Reservation> findAllByUser(User user);

    @Query("select new com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto(" +
            "m.name, r.serviceDate, u.name, d.name, r.payMethod ,r.price) " +
            "from Reservation r " +
            "join r.user u " +
            "join r.shop s " +
            "join r.menu m " +
            "join r.designer d " +
            "where r.id = :reservationId")
    Optional<ShopReservationDetailResponseDto> findDetailById(@Param("reservationId") UUID reservationId);


    @Query("Select r from Reservation r Where r.designer = :designer and r.serviceDate between :startDate and :endDate")
    List<Reservation> findByDesignerAndTime(@Param("designer")Designer designer,
                                            @Param("startDate")LocalDateTime startDate,
                                            @Param("endDate")LocalDateTime endDate);
}
