package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Certification;
import com.myong.backend.domain.entity.designer.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.cert.Certificate;
import java.util.*;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {
    List<Certification> findByResume(Resume resume);

    void deleteByName(String name);
}
