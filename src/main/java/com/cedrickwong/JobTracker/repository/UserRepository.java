package com.cedrickwong.JobTracker.repository;

import com.cedrickwong.JobTracker.model.User;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("Update User u " +
            "SET u.email = COALESCE(:email, u.email), " +
                "u.password = COALESCE(:password, u.password), " +
                "u.firstName = COALESCE(:firstName, u.firstName), " +
                "u.lastName = COALESCE(:lastName, u.lastName) " +
            "WHERE u.id = :id")
    void update(Long id, String email, String password, String firstName, String lastName);
}
