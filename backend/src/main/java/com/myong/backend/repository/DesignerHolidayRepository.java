package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerHoliday;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DesignerHolidayRepository extends JpaRepository<DesignerHoliday, UUID> {

    @Query("Select d from DesignerHoliday d Where d.designer = :designer AND d.date between :startDate AND:endDate")
    List<DesignerHoliday> findAllByDesignerAndMonth(
            @Param("designer")Designer designer,
            @Param("startDate")LocalDate startDate,
            @Param("endDate")LocalDate endDate);

    void deleteByDesigner(Designer designer);

    Optional<DesignerHoliday> findByDesigner(Designer designer);
}
