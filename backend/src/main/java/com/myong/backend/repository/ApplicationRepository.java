package com.myong.backend.repository;

import com.myong.backend.domain.entity.shop.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
}
