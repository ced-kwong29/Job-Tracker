package com.cedrickwong.backend.repository;

import com.cedrickwong.backend.model.Company;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);

    @Modifying
    @Transactional
    @Query("Update Company c " +
            "SET c.name = :name " +
            "WHERE c.id = :id")
    void update(Long id, String name);
}
