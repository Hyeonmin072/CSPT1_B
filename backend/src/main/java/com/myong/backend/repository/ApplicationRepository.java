package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Application;
import com.myong.backend.domain.entity.shop.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByJobPost(JobPost jobPost);
}
