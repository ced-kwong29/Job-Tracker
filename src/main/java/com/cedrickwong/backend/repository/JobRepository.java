package com.cedrickwong.backend.repository;

import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.Job.Type;
import com.cedrickwong.backend.model.Company;

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
            "WHERE j.company = :company AND " +
                    "j.title = :title AND " +
                    "j.type = :type")
    Optional<Job> findByCompanyTitleType(Company company, String title, Type type);
}
