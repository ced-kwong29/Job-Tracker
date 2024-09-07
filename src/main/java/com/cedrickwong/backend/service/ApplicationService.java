package com.cedrickwong.backend.service;

import com.cedrickwong.backend.model.Application;
import com.cedrickwong.backend.model.Application.Status;
import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.User;
import com.cedrickwong.backend.model.Job.Type;
import com.cedrickwong.backend.repository.ApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

    public void update(Application application, Job job, LocalDate date, Status status) {
        if (job != null) {
            application.setJob(job);
        }
        if (date != null) {
            application.setDate(date);
        }
        if (status != null) {
            application.setStatus(status);
        }
        applicationRepository.update(application.getId(), job, date, status);
    }
}
