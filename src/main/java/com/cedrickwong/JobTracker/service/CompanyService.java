package com.cedrickwong.JobTracker.service;

import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Optional<Company> getByName(String name) {
        return companyRepository.findByName(name);
    }

    public void save(Company company) {
        companyRepository.save(company);
    }

    public void delete(Company company) {
        companyRepository.delete(company);
    }
}
