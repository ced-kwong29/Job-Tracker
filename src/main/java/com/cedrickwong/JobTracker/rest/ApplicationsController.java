package com.cedrickwong.JobTracker.rest;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Application.Status;
import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.model.Job;
import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.service.ApplicationService;
import com.cedrickwong.JobTracker.service.CompanyService;
import com.cedrickwong.JobTracker.service.JobService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/applications")
public class ApplicationsController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final HttpSession httpSession;

    public ApplicationsController(ApplicationService applicationService, CompanyService companyService, JobService jobService, HttpSession httpSession) {
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.httpSession = httpSession;
    }

    @GetMapping
    public ResponseEntity<?> getAllApplicationsOfUser() {
        User user = (User) httpSession.getAttribute("user");
        return ResponseEntity.ok(user == null ? "User is not logged in" : applicationService.getAllByUser(user));
    }

    @GetMapping("/app")
    public ResponseEntity<?> getApplication(@RequestParam Long id) {
        Optional<Application> application = applicationService.getById(id);
        return ResponseEntity.ok(application.isEmpty() ? "Invalid application id" : application.get());
    }

    @GetMapping("/create")
    public ResponseEntity<?> createApplication(@RequestParam String companyName, @RequestParam String jobTitle, @RequestParam(required = false) Date date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Company> companySearch = companyService.getByName(companyName);
        Job job = companySearch.map(company -> jobService.getFromCompanyByTitle(company, jobTitle).orElseGet(() -> createJob(company, jobTitle)))
                            .orElseGet(() -> createJob(createCompany(companyName), jobTitle));

        return ResponseEntity.ok("Successfully created:\n" + new Application(user, job, date == null ? new Date() : date, status == null ? Status.WAITING : status));
    }

    private Company createCompany(String name) {
        Company company = new Company(name);
        companyService.save(company);
        return company;
    }

    private Job createJob(Company company, String title) {
        Job job = new Job(company, title);
        jobService.save(job);
        return job;
    }

    @GetMapping("/app/{interact}")
    public ResponseEntity<?> interactApplication(@PathVariable String interact, @RequestParam Long id, @RequestParam(required = false) Status status) {
        if (httpSession.getAttribute("user") == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Application> applicationSearch = applicationService.getById(id);
        if (applicationSearch.isEmpty()) {
            return ResponseEntity.ok("Invalid application id");
        }

        if (interact.equals("delete")) {
            applicationService.delete(applicationSearch.get());
            return ResponseEntity.ok("Successfully deleted:\n" + applicationSearch.get());
        }

        if (interact.equals("update")) {
            if (status == null) {
                return ResponseEntity.ok("Please provide updated status");
            }
            Application application = applicationSearch.get();
            application.setStatus(status);
            applicationService.update(application, status);
            return ResponseEntity.ok("Successfully updated:\n" + application);
        }

        return ResponseEntity.badRequest().build();
    }
}
