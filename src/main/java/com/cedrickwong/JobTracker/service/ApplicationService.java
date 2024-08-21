package com.cedrickwong.JobTracker.service;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.repository.ApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Optional<Application> getById(Long id) {
        return applicationRepository.findById(id);
    }

    public List<Application> getAllByUser(User user) {
        return applicationRepository.findByUser(user);
    }

    public List<Application> getAllByUserAndStatus(User user, Application.Status status) {
        return applicationRepository.findByUserAndStatus(user, status);
    }

    public List<Application> getAllByUserAndDates(User user, LocalDate startDate, LocalDate endDate) {
        return applicationRepository.findByUserAndDates(user, startDate, endDate);
    }

    public List<Application> getAllByUserAndCompany(User user, Company company) {
        return applicationRepository.findByUserAndCompany(user, company);
    }

    public List<Application> getAllByUserAndJobTitle(User user, String jobTitle) {
        return applicationRepository.findByUserAndJobTitle(user, jobTitle);
    }

    public void save(Application application) {
        applicationRepository.save(application);
    }

    public void delete(Application application) {
        applicationRepository.delete(application);
    }

    public void deleteAllFromUser(User user) {
        applicationRepository.deleteAll(applicationRepository.findByUser(user));
    }

    public void update(Application application, Application.Status status) {
        application.setStatus(status);
        applicationRepository.updateStatus(application.getId(), status);
    }
}
