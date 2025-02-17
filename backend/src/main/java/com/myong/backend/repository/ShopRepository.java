package com.myong.backend.repository;

import com.myong.backend.domain.dto.shop.ShopProfileResponseDto;
import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    Optional<Shop> findByEmail(String email);


    @Query("select new com.myong.backend.domain.dto.shop.ShopProfileResponseDto(s.name, s.address, s.post, s.tel, s.pwd, s.desc, s.openTime , s.closeTime, s.regularHoliday)" +
            "from Shop s " +
            "where s.email = :email")
    ShopProfileResponseDto findProfileByEmail(@Param("email")String email);
}
