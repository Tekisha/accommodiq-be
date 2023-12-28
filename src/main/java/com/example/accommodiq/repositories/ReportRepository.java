package com.example.accommodiq.repositories;

import com.example.accommodiq.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Transactional
    void deleteByReportedUserId(Long id);
    @Transactional
    void deleteByReportingUserId(Long id);
}
