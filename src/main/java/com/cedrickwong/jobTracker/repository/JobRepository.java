package com.cedrickwong.jobTracker.repository;

import com.cedrickwong.jobTracker.model.Company;
import com.cedrickwong.jobTracker.model.Job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByTitle(String title);
    List<Job> findByCompany(Company company);
}
