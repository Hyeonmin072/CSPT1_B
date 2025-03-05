package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    @Query("select jp " +
            "from JobPost jp join fetch jp.shop s " +
            "where s.id = :shopId")
    List<JobPost> findByShop(@Param("shopId") UUID uuid);
}
