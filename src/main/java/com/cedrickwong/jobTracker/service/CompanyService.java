package com.cedrickwong.jobTracker.service;

import com.cedrickwong.jobTracker.model.Company;
import com.cedrickwong.jobTracker.repository.CompanyRepository;

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

    public void saveCompany(Company company) {
        companyRepository.save(company);
    }

    public void deleteCompany(Company company) {
        companyRepository.delete(company);
    }
}
