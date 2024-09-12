package com.cedrickwong.backend.repository;

import com.cedrickwong.backend.model.Application;
import com.cedrickwong.backend.model.Application.Status;
import com.cedrickwong.backend.model.Company;
import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.User;
import com.cedrickwong.backend.model.Job.Type;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>{
    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.user = :user AND " +
                "((:startDate IS NULL AND :endDate IS NULL) OR a.date BETWEEN :startDate AND :endDate) AND " +
                "(:companyName IS NULL OR a.job.company.name = :companyName) AND " +
                "(:jobTitle IS NULL OR a.job.title = :jobTitle) AND " +
                "(:status is NULL OR a.status = :status) AND " +
                "(:type IS NULL OR a.job.type = :type)")
    List<Application> findByUser(User user, LocalDate startDate, LocalDate endDate, String companyName, String jobTitle, Status status, Type type);

    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.job.title = :jobTitle")
    List<Application> findByJobTitle(String jobTitle);

    @Query("SELECT COUNT(a) " +
            "FROM Application a " +
            "WHERE a.job.company = :company AND " +
                    "(:jobTitle IS NULL OR a.job.title = :jobTitle)")
    int countByCompanyAndJobTitle(Company company, String jobTitle);

    @Modifying
    @Transactional
    @Query("Update Application a " +
            "SET a.date = COALESCE(:date, a.date), " +
            "a.status = COALESCE(:status, a.status) " +
            "WHERE a.id = :id")
    void update(Long id, LocalDate date, Status status);
}
