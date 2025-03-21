package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, UUID> {

    @Query("select b from BlackList b join fetch b.user where b.shop = :shop")
    List<BlackList> findByShop(@Param("shop") Shop shop);

    Optional<BlackList> findByShopAndUser(Shop shop, User user);
}
