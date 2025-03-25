package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Attendance;
import com.myong.backend.domain.entity.designer.Designer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    void deleteByDesigner(Designer designer);

    List<Attendance> findByDesigner(Designer designer);
}
