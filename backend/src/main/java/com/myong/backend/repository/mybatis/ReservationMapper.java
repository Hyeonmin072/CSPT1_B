package com.myong.backend.repository.mybatis;

import com.myong.backend.domain.dto.reservation.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.ShopReservationResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

    List<ShopReservationResponseDto> findAll(@Param("request") ShopReservationRequestDto request);
}
