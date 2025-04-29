package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DesignerRepository extends JpaRepository<Designer, UUID> {
    Boolean existsByEmail(String email);
    Boolean existsByNickName(String nickName);
    Optional<Designer> findByEmail(String email);

    @Query("Select count(d) from Designer d")
    long count();

    @Query("Select d From Designer d Order by d.score desc")
    List<Designer> findTopDesigners(Pageable pageable);
}


