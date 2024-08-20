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
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    void updateEmail(Long id, String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePassword(Long id, String password);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :firstName WHERE u.id = :id")
    void updateFirstName(Long id, String firstName);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastName = :lastName WHERE u.id = :id")
    void updateLastName(Long id, String lastName);
}
