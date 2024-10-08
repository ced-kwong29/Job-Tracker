package com.cedrickwong.backend.service;

import com.cedrickwong.backend.model.Company;
import com.cedrickwong.backend.repository.CompanyRepository;

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

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public void delete(Company company) {
        companyRepository.delete(company);
    }

    public void update(Company company, String name) {
        company.setName(name);
        companyRepository.update(company.getId(), name);
    }
}
