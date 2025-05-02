package com.myong.backend.repository.mybatis;

import com.myong.backend.domain.dto.shop.ShopAttendanceRequestDto;
import com.myong.backend.domain.dto.shop.ShopAttendanceResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceMapper {
    List<ShopAttendanceResponseDto> findAll(@Param("request") ShopAttendanceRequestDto request);
}
