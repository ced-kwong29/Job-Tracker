package com.cedrickwong.jobTracker.service;

import com.cedrickwong.jobTracker.model.Company;
import com.cedrickwong.jobTracker.model.Job;
import com.cedrickwong.jobTracker.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Optional<Job> getById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> getByTitle(String title) {
        return jobRepository.findByTitle(title);
    }

    public List<Job> getAllFromCompany(Company company) {
        return jobRepository.findByCompany(company);
    }

    public void save(Job job) {
        jobRepository.save(job);
    }

    public void delete(Job job) {
        jobRepository.delete(job);
    }

    public void deleteAllFromCompany(Company company) {
        jobRepository.deleteAll(jobRepository.findByCompany(company));
    }
}
