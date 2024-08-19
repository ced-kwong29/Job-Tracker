package com.cedrickwong.jobTracker.repository;

import com.cedrickwong.jobTracker.model.Application;
import com.cedrickwong.jobTracker.model.User;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);

    @Modifying
    @Transactional
    @Query("Update Application a SET a.status = :status WHERE a.id = :id")
    void updateStatus(Long id, Application.Status status);
}
