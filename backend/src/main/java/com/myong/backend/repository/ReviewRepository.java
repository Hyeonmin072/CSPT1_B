package com.myong.backend.repository;

import com.myong.backend.domain.dto.designer.data.ReviewData;
import com.myong.backend.domain.dto.user.response.DesignerReviewImageResponseDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.usershop.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("Select count(r) from Review r")
    long count();


    // 해당 디자이너의 유저 성별에 맞는 랜덤한 리뷰 이미지 가져오는 로직
    @Query(value = """
            SELECT r.rv_image
            FROM review r
            JOIN user u ON r.u_id = u.u_id
            WHERE r.d_id = :designerId
              AND u.u_gender = :userGender
            ORDER BY RAND()
            LIMIT 5
            """, nativeQuery = true)
    List<String> findRandomReviewImagesForDesigner(
            @Param("designerId") UUID designerId,
            @Param("userGender") String userGender
    );



    // 리뷰 이미지 가져오기
    @Query("select new com.myong.backend.domain.dto.user.response.DesignerReviewImageResponseDto(" +
            "r.image) " +
            "from Review r " +
            "where r.designer = :designer and r.image <> '' " +
            "order by r.rating desc")
    List<DesignerReviewImageResponseDto> findReviewImages(@Param("designer")Designer designer,Pageable pageable);


    // 리뷰데이터로 가공해서 받기
    @Query("Select new com.myong.backend.domain.dto.designer.data.ReviewData(r.user.name, r.image, r.rating, r.content, res.menu.name) "+
            "From Review r Join r.reservation res " +
            "Where r.designer.email = :designerEmail")
    List<ReviewData> findAllByDesignerEmail(@Param("designerEmail") String email);


    @Query("select r.designer , count(r.id)" +
            "from Review r " +
            "where r.createDate between :startDate and :endDate " +
            "group by r.designer.id " +
            "order by count(r.id) desc")
    List<Object[]> findDesignerWithReviewCountBetweenDates(@Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate, Pageable pageable);
}
