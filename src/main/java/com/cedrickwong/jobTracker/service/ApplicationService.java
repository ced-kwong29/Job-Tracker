package com.cedrickwong.jobTracker.service;

import com.cedrickwong.jobTracker.model.Application;
import com.cedrickwong.jobTracker.model.Company;
import com.cedrickwong.jobTracker.model.Job;
import com.cedrickwong.jobTracker.model.User;
import com.cedrickwong.jobTracker.repository.ApplicationRepository;

import com.cedrickwong.jobTracker.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
    }

    public Optional<Application> getById(Long id) {
        return applicationRepository.findById(id);
    }

    public void update(Application application, Application.Status status) {
        application.setStatus(status);
        applicationRepository.updateStatus(application.getId(), status);
    }

    public List<Application> getByUser(User user) {
        return applicationRepository.findByUser(user);
    }

    public List<Application> getByStatus(Application.Status status) {
        return applicationRepository.findByStatus(status);
    }

    public List<Application> getByDates(Date startDate, Date endDate) {
        return applicationRepository.findBetweenDates(startDate, endDate);
    }

    public List<Application> getByCompany(Company company) {
        return applicationRepository.findByCompany(company);
    }

    public List<Application> getByJobTitle(String jobTitle) {
        return applicationRepository.findByJobTitle(jobTitle);
    }

    public void save(Application application) {
        applicationRepository.save(application);
    }

    public void delete(Application application) {
        applicationRepository.delete(application);
    }
}
