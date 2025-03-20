package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Career;
import com.myong.backend.domain.entity.designer.Resume;
import com.myong.backend.domain.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.*;

public interface CareerRepository extends JpaRepository<Career, UUID> {
    List<Career> findByResume(Resume resume);
    Optional<Career> findByResumeAndNameAndJoinDate(Resume resume, String name, LocalDate joinDate);
}
