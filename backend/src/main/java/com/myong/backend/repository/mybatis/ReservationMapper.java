package com.myong.backend.repository.mybatis;

import com.myong.backend.domain.dto.reservation.request.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationMyBatisResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

    List<ShopReservationMyBatisResponseDto> findAll(@Param("shopEmail") String email, @Param("request") ShopReservationRequestDto request);
}
