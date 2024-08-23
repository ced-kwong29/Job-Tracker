package com.cedrickwong.JobTracker.service;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Application.Status;
import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.model.Job.Type;
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

    public List<Application> getAllByUser(User user, LocalDate startDate, LocalDate endDate, String companyName, String jobTitle, Status status, Type type) {
        return applicationRepository.findByUser(user, startDate, endDate, companyName, jobTitle, status, type);
    }

    public void save(Application application) {
        applicationRepository.save(application);
    }

    public void delete(Application application) {
        applicationRepository.delete(application);
    }

    public void update(Application application, Application.Status status) {
        application.setStatus(status);
        applicationRepository.updateStatus(application.getId(), status);
    }
}
