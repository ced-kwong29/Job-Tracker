package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.model.Application;
import com.cedrickwong.jobTracker.model.User;
import com.cedrickwong.jobTracker.service.ApplicationService;
import com.cedrickwong.jobTracker.service.CompanyService;
import com.cedrickwong.jobTracker.service.JobService;
import com.cedrickwong.jobTracker.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/applications")
public class ApplicationsController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final UserService userService;
    private final HttpSession httpSession;

    public ApplicationsController(ApplicationService applicationService, CompanyService companyService, JobService jobService, UserService userService, HttpSession httpSession) {
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.userService = userService;
        this.httpSession = httpSession;
    }

    @GetMapping
    public ResponseEntity<?> getAllApplicationsOfUser() {
        User user = (User) httpSession.getAttribute("user");
        return ResponseEntity.ok(user == null ? "User is not logged in" : applicationService.getByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplication(@PathVariable String id) {
        Optional<Application> application = applicationService.getById(Long.valueOf(id));
        return ResponseEntity.ok(application.isEmpty() ? "Invalid application id" : application.get());
    }


}
