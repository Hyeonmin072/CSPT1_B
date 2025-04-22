package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    Optional<Shop> findByEmail(String email);

    @Query("Select count(s) from Shop s")
    long count();

    @Query(value = " select * FROM shop " +
            "Where ST_Distance_Sphere(point(:longitude,:latitude),point(shop.s_longitude,shop.s_latitude)) <= 2000",
            nativeQuery = true)
    List<Shop> findShopWithinRadius(@Param("longitude") double longitude, @Param("latitude") double latitude);

    @Query("select s From Shop s where s.address Like :location% ")
    List<Shop> findShopWithAddress(@Param("location") String location);

    @Query("select s From Shop s Where s.reviewCount >= 0 Order by s.rating Desc ")
    List<Shop> findTopShops(Pageable pageable);

    Optional<Shop> findByBizId(String bizId);
}
