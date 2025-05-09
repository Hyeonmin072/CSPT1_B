package com.myong.backend.repository;

import com.myong.backend.domain.dto.user.data.DesignerListData;
import com.myong.backend.domain.entity.designer.Designer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DesignerRepository extends JpaRepository<Designer, UUID> {
    Boolean existsByEmail(String email);
    Boolean existsByNickName(String nickName);
    Optional<Designer> findByEmail(String email);

    @Query("Select count(d) from Designer d")
    long count();

    @Query("select new com.myong.backend.domain.dto.user.data.DesignerListData(" +
            "d.email, d.nickName, d.desc, d.like, d.rating, d.image) " +
            "from Designer d " +
            "order by d.score desc")
    List<DesignerListData> findTopDesigners(Pageable pageable);



    @Query(value = "select * " +
            "from designer d " +
            "where ST_Distance_Sphere(point(:longitude,:latitude),point(d.d_longitude,d.d_latitude)) <= 5000 " +
            "order by d.d_score desc",
             nativeQuery = true)
    List<Designer> findDesignersForUser(@Param("longitude")double longitude, @Param("latitude")double latitude, Pageable pageable);


}


