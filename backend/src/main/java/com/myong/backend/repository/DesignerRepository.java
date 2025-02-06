package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DesignerRepository extends JpaRepository<Designer, UUID> {
}
