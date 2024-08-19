package com.cedrickwong.jobTracker.service;

import com.cedrickwong.jobTracker.model.Application;
import com.cedrickwong.jobTracker.model.User;
import com.cedrickwong.jobTracker.repository.ApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void update(Application application, Application.Status status) {
        application.setStatus(status);
        applicationRepository.updateStatus(application.getId(), status);
    }

    public List<Application> getByUser(User user) {
        return applicationRepository.findByUser(user);
    }

    public void save(Application application) {
        applicationRepository.save(application);
    }

    public void delete(Application application) {
        applicationRepository.delete(application);
    }
}
