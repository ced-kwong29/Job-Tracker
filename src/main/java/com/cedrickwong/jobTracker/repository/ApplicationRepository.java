package com.cedrickwong.jobTracker.repository;

import com.cedrickwong.jobTracker.model.Application;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findApplicationsByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("Update Application a SET a.status = :status WHERE a.id = :id")
    void updateApplicationByStatus(Long id, Application.Status status);
}
