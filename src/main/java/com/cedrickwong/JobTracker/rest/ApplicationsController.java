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

    @GetMapping("/apps")
    public ResponseEntity<?> getAllFromUser() {
        User user = (User) httpSession.getAttribute("user");
        return ResponseEntity.ok(user == null ? "User is not logged in" : applicationService.getAllByUser(user));
    }

    @GetMapping("/app")
    public ResponseEntity<?> get(@RequestParam Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }
        Optional<Application> applicationSearch = applicationService.getById(id);
        if (applicationSearch.isEmpty()) {
            return ResponseEntity.ok("Invalid application id");
        }
        Application application = applicationSearch.get();
        return ResponseEntity.ok(!user.equals(application.getUser()) ? "Unauthorized" : application);
    }

    @GetMapping("/between")
    public ResponseEntity<?> getBetweenDates(@RequestParam String start, @RequestParam String end) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        LocalDate startDate = LocalDate.parse(start), endDate = LocalDate.parse(end);
        return ResponseEntity.ok(startDate.isAfter(endDate) ? "Start date cannot be after end date" : applicationService.getAllByUserAndDates(user, startDate, endDate));
    }

    @GetMapping("/to")
    public ResponseEntity<?> getByCompany(@RequestParam String companyName) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Company> companySearch = companyService.getByName(companyName);
        return ResponseEntity.ok(companySearch.isEmpty() ? "Invalid company name" : applicationService.getAllByUserAndCompany(user, companySearch.get()));
    }

    @GetMapping("/for")
    public ResponseEntity<?> getByJobTitle(@RequestParam String jobTitle) {
        User user = (User) httpSession.getAttribute("user");
        return ResponseEntity.ok(user == null ? "User is not logged in" : applicationService.getAllByUserAndJobTitle(user, jobTitle));
    }

    @GetMapping("/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }
        return ResponseEntity.ok(applicationService.getAllByUserAndStatus(user, status));
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam String companyName, @RequestParam String jobTitle, @RequestParam Type type, @RequestParam(required = false) String date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Company> companySearch = companyService.getByName(companyName);
        Job job = companySearch.map(company -> jobService.getFromCompanyByTitle(company, jobTitle).orElseGet(() -> createJob(company, jobTitle, type)))
                            .orElseGet(() -> createJob(createCompany(companyName), jobTitle, type));

        Application application = new Application(user, job, date == null ? LocalDate.now() : LocalDate.parse(date), status == null ? Status.WAITING : status);
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

    @PostMapping("/{interact}")
    public ResponseEntity<String> interact(@PathVariable String interact, @RequestParam Long id, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Optional<Application> applicationSearch;
        Application application;
        switch(interact) {
            case "deleteAll":
                applicationService.deleteAllFromUser(user);
                return ResponseEntity.ok("Successfully deleted all applications:");

            case "delete":
                applicationSearch = applicationService.getById(id);
                if (applicationSearch.isEmpty()) {
                    return ResponseEntity.ok("Invalid application id");
                }
                application = applicationSearch.get();
                if (!user.equals(application.getUser())) {
                    return ResponseEntity.ok("Unauthorized access to application");
                }
                applicationService.delete(application);
                return ResponseEntity.ok("Successfully deleted:\n" + application);

            case "update":
                applicationSearch = applicationService.getById(id);
                if (applicationSearch.isEmpty()) {
                    return ResponseEntity.ok("Invalid application id");
                }
                application = applicationSearch.get();
                if (!user.equals(application.getUser())) {
                    return ResponseEntity.ok("Unauthorized access to application");
                }
                if (status == null) {
                    return ResponseEntity.ok("Please provide updated status");
                }
                applicationService.update(application, status);
                return ResponseEntity.ok("Successfully updated:\n" + application);
        }

        return ResponseEntity.badRequest().build();
    }
}
