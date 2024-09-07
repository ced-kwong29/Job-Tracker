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
    Optional<Job> findByCompanyTitleType(Company company, String title);

    @Modifying
    @Transactional
    @Query("UPDATE Job j " +
            "SET j.company = :company " +
            "WHERE j.id = :id")
    void updateCompany(Long id, Company company);

    @Modifying
    @Transactional
    @Query("Update Job j " +
            "SET j.title = CASE WHEN :title IS NOT NULL THEN :title ELSE j.title END, " +
                "j.type = CASE WHEN :type IS NOT NULL THEN :type ELSE j.type END " +
            "WHERE j.id = :id")
    void update(Long id, String title, Type type);
}
