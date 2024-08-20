package com.cedrickwong.jobTracker.repository;

import com.cedrickwong.jobTracker.model.Application;
import com.cedrickwong.jobTracker.model.Company;
import com.cedrickwong.jobTracker.model.Job;
import com.cedrickwong.jobTracker.model.User;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);
    List<Application> findByStatus(Application.Status status);
    List<Application> findBetweenDates(Date startDate, Date endDate);

//    @Query("SELECT a FROM Application a JOIN a.job j WHERE j.title = :jobTitle")
    @Query("SELECT a FROM Application a WHERE a.job.title = :jobTitle")
    List<Application> findByJobTitle(@Param("jobTitle") String jobTitle);

    @Query("SELECT a FROM Application a WHERE a.job.company = :company")
    List<Application> findByCompany(Company company);

    @Modifying
    @Transactional
    @Query("Update Application a SET a.status = :status WHERE a.id = :id")
    void updateStatus(Long id, Application.Status status);
}
