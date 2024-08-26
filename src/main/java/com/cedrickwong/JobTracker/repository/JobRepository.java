package com.cedrickwong.JobTracker.repository;

import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.model.Job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByTitle(String title);
    List<Job> findByCompany(Company company);

    @Query("SELECT j " +
            "FROM Job j " +
            "WHERE j.company = :company " +
            "AND j.title = :title")
    Optional<Job> findByCompanyAndTitle(Company company, String title);
}
