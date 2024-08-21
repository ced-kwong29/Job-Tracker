package com.cedrickwong.JobTracker.rest;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Application.Status;
import com.cedrickwong.JobTracker.service.ApplicationService;
import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.service.CompanyService;
import com.cedrickwong.JobTracker.model.Job;
import com.cedrickwong.JobTracker.model.Job.Type;
import com.cedrickwong.JobTracker.service.JobService;
import com.cedrickwong.JobTracker.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/applications")
public class ApplicationsController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final HttpSession httpSession;

    @Autowired
    public ApplicationsController(ApplicationService applicationService, CompanyService companyService, JobService jobService, HttpSession httpSession) {
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.httpSession = httpSession;
    }

    @GetMapping
    public ResponseEntity<?> getAllFromUser() {
        User user = (User) httpSession.getAttribute("user");
        return ResponseEntity.ok(user == null ? "User is not logged in" : applicationService.getAllByUser(user));
    }

    @GetMapping("/app")
    public ResponseEntity<?> get(@RequestParam Long id) {
        Optional<Application> application = applicationService.getById(id);
        return ResponseEntity.ok(application.isEmpty() ? "Invalid application id" : application.get());
    }

    @GetMapping("/between")
    public ResponseEntity<?> getBetweenDates(@RequestParam String start, @RequestParam String end) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        LocalDate startDate = LocalDate.parse(start), endDate = LocalDate.parse(end);
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.ok("Start date cannot be after end date");
        }
        return ResponseEntity.ok(applicationService.getAllByUserAndDates(user, startDate, endDate));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam String companyName, @RequestParam String jobTitle, @RequestParam Type type, @RequestParam(required = false) LocalDate date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Company> companySearch = companyService.getByName(companyName);
        Job job = companySearch.map(company -> jobService.getFromCompanyByTitle(company, jobTitle).orElseGet(() -> createJob(company, jobTitle, type)))
                            .orElseGet(() -> createJob(createCompany(companyName), jobTitle, type));

        Application application = new Application(user, job, date == null ? LocalDate.now() : date, status == null ? Status.WAITING : status);
        applicationService.save(application);
        return ResponseEntity.ok("Successfully created:\n" + application);
    }

    private Company createCompany(String name) {
        Company company = new Company(name);
        companyService.save(company);
        return company;
    }

    private Job createJob(Company company, String title, Type type) {
        Job job = new Job(company, title, type);
        jobService.save(job);
        return job;
    }

    @PostMapping("/app/{interact}")
    public ResponseEntity<?> interact(@PathVariable String interact, @RequestParam Long id, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Application> applicationSearch;
        switch(interact) {
            case "deleteAll":
                applicationService.deleteAllFromUser(user);
                return ResponseEntity.ok("Successfully deleted all applications:");

            case "delete":
                applicationSearch = applicationService.getById(id);
                if (applicationSearch.isEmpty()) {
                    return ResponseEntity.ok("Invalid application id");
                }
                applicationService.delete(applicationSearch.get());
                return ResponseEntity.ok("Successfully deleted:\n" + applicationSearch.get());

            case "update":
                applicationSearch = applicationService.getById(id);
                if (applicationSearch.isEmpty()) {
                    return ResponseEntity.ok("Invalid application id");
                }
                if (status == null) {
                    return ResponseEntity.ok("Please provide updated status");
                }
                Application application = applicationSearch.get();
                applicationService.update(application, status);
                return ResponseEntity.ok("Successfully updated:\n" + application);
        }

        return ResponseEntity.badRequest().build();
    }
}
