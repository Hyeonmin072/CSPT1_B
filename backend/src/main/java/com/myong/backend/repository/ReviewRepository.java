package com.myong.backend.repository;

import com.myong.backend.domain.dto.designer.data.ReviewData;
import com.myong.backend.domain.entity.usershop.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("Select count(r) from Review r")
    long count();


    // 리뷰데이터로 가공해서 받기
    @Query("Select new com.myong.backend.domain.dto.designer.data.ReviewData(r.user.name, r.image, r.rating, r.content, res.menu.name) "+
            "From Review r Join r.reservation res " +
            "Where r.designer.email = :designerEmail")
    List<ReviewData> findAllByDesignerEmail(@Param("designerEmail") String email);
}
