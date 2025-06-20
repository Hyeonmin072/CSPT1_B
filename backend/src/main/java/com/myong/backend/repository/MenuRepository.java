package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.MenuCategory;
import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findByShop(Shop shop); // dto로 변환 시 fetch join 불가 -> 일반 join 사용

    List<Menu> findByDesignerAndCategory(Designer designer, MenuCategory category);

    List<Menu> findByDesignerAndRecommend(Designer designer, boolean Recommend);

    List<Menu> findByDesigner(Designer designer);
}
