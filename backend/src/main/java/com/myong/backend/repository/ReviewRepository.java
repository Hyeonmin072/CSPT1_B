package com.myong.backend.repository;

import com.myong.backend.domain.entity.usershop.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("Select count(r) from Review r")
    long count();
}
