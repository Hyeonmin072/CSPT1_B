package com.myong.backend.repository;

import com.myong.backend.domain.dto.menu.MenuListResponseDto;
import com.myong.backend.domain.entity.shop.Menu;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    @Query("select new com.myong.backend.domain.dto.menu.MenuListResponseDto(m.name, d.name, m.price) " +
            "from Menu m join m.designer d join m.shop s " +
            "where s.email = :email")
    List<MenuListResponseDto> findMenuByShopEmail(@Param("email")String email);

    Menu findByName(@NotBlank String name);
}
