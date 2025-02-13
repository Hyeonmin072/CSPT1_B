package com.myong.backend.repository;

import com.myong.backend.domain.dto.event.EventListResponseDto;
import com.myong.backend.domain.entity.shop.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("select new com.myong.backend.domain.dto.event.EventListResponseDto(e.name, e.amount, e.type, e.startDate, e.endDate) " +
            "from Event e join e.shop s " +
            "where s.id = :shopId")
    List<EventListResponseDto> findByShop(@Param("shopId") UUID uuid);
}
