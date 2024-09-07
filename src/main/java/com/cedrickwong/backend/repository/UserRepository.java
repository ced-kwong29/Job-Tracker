package com.cedrickwong.backend.repository;

import com.cedrickwong.backend.model.User;

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
            "SET u.email = CASE WHEN :email IS NOT NULL THEN :email ELSE u.email END, " +
                "u.password = CASE WHEN :password IS NOT NULL THEN :password ELSE u.password END, " +
                "u.firstName = CASE WHEN :firstName IS NOT NULL THEN :firstName ELSE u.firstName END, " +
                "u.lastName = CASE WHEN :lastName IS NOT NULL THEN :lastName ELSE u.lastName END " +
            "WHERE u.id = :id")
    void update(Long id, String email, String password, String firstName, String lastName);
}
