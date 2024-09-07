package com.cedrickwong.backend.repository;

import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.Job.Type;
import com.cedrickwong.backend.model.Company;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "WHERE j.company = :company AND " +
                    "j.title = :title")
    Optional<Job> findByCompanyAndTitle(Company company, String title);

    @Modifying
    @Transactional
    @Query("Update Job j " +
            "SET j.company = CASE WHEN :company IS NOT NULL THEN :company ELSE j.company END, " +
                "j.title = CASE WHEN :title IS NOT NULL THEN :title ELSE j.title END, " +
                "j.type = CASE WHEN :type IS NOT NULL THEN :type ELSE j.type END " +
            "WHERE j.id = :id")
    void update(Long id, Company company, String title, Type type);
}
