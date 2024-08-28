package com.cedrickwong.JobTracker.repository;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Application.Status;
import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.model.Job.Type;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.user = :user AND " +
                "((:startDate IS NULL AND :endDate IS NULL) OR a.date BETWEEN :startDate AND :endDate) AND " +
                "(:companyName IS NULL OR a.job.company.name = :companyName) AND " +
                "(:jobTitle IS NULL OR a.job.title = :jobTitle) AND " +
                "(:status is NULL OR a.status = :status) AND " +
                "(:type IS NULL OR a.job.type = :type)")
    List<Application> findByUser(User user, LocalDate startDate, LocalDate endDate, String companyName, String jobTitle, Status status, Type type);

    @Modifying
    @Transactional
    @Query("Update Application a " +
            "SET a.status = :status " +
            "WHERE a.id = :id")
    void updateStatus(Long id, Status status);
}
