package com.cedrickwong.JobTracker.service;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.repository.ApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public List<Application> getAllByStatus(Application.Status status) {
        return applicationRepository.findByStatus(status);
    }

    public List<Application> getAllByDates(Date startDate, Date endDate) {
        return applicationRepository.findBetweenDates(startDate, endDate);
    }

    public List<Application> getAllByCompany(Company company) {
        return applicationRepository.findByCompany(company);
    }

    public List<Application> getAllByJobTitle(String jobTitle) {
        return applicationRepository.findByJobTitle(jobTitle);
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
