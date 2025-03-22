package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerRegularHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DesignerRegularHolidayRepository extends JpaRepository<DesignerRegularHoliday, UUID> {

    Optional<DesignerRegularHoliday> findByDesigner(Designer designer);

    void deleteByDesigner(Designer designer);
}
