package com.cedrickwong.JobTracker.repository;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Application.Status;
import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.model.User;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);

    @Query("SELECT a FROM Application a WHERE a.user = :user AND a.status = :status")
    List<Application> findByUserAndStatus(User user, Status status);

    @Query("SELECT a FROM Application a WHERE a.user = :user AND a.date BETWEEN :startDate AND :endDate")
    List<Application> findByUserAndDates(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Application a WHERE a.user = :user AND a.job.title = :jobTitle")
    List<Application> findByUserAndJobTitle(User user, @Param("jobTitle") String jobTitle);

    @Query("SELECT a FROM Application a WHERE a.user = :user AND a.job.company = :company")
    List<Application> findByUserAndCompany(User user, Company company);

    @Modifying
    @Transactional
    @Query("Update Application a SET a.status = :status WHERE a.id = :id")
    void updateStatus(Long id, Application.Status status);
}
