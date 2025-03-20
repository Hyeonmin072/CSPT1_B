package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.DesignerWantedDay;
import com.myong.backend.domain.entity.designer.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface DesignerWantedDayRepository extends JpaRepository<DesignerWantedDay, UUID> {
    List<DesignerWantedDay> findByResume(Resume resume);
}
