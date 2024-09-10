package com.cedrickwong.backend.service;

import com.cedrickwong.backend.model.Company;
import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.Job.Type;
import com.cedrickwong.backend.repository.JobRepository;

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

    public Optional<Job> getByCompanyTitleType(Company company, String title, Type type) {
        return jobRepository.findByCompanyTitleType(company, title, type);
    }

//    public void save(Job job) {
//        jobRepository.save(job);
//    }
    public Job save(Job job) {
        return jobRepository.save(job);
    }

    public void delete(Job job) {
        jobRepository.delete(job);
    }

    public void update(Job job, Company company, Type type, String title) {
        if (company != null) {
            job.setCompany(company);
        }
        if (title != null) {
            job.setTitle(title);
        }
        if (type != null) {
            job.setType(type);
        }
        jobRepository.update(job.getId(), company, title, type);
    }

    public void deleteAllFromCompany(Company company) {
        jobRepository.deleteAll(jobRepository.findByCompany(company));
    }
}
