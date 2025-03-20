package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DesignerRepository extends JpaRepository<Designer, UUID> {
    Boolean existsByEmail(String email);
    Boolean existsByNickName(String nickName);
    Optional<Designer> findByNickName(String nickName);
    Optional<Designer> findByEmail(String email);
}